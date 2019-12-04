package dm.uporov.machete.apt.utils

import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.exception.ClassIsNotAnnotatedException
import kotlin.reflect.KClass

fun Symbol.TypeSymbol.asApplicationFeature(internalDependencies: List<Symbol.TypeSymbol>) =
    asFeature(
        featureAnnotation = MacheteApplication::class,
        internalDependencies = internalDependencies,
        recursive = true,
        deepRecursive = true
    ).let {
        it.copy(
            dependencies = emptyList(),
            internalDependencies = (it.dependencies + it.internalDependencies).distinct()
        )
    }

fun Symbol.TypeSymbol.asFeature(internalDependencies: List<Symbol.TypeSymbol>) = asFeature(
        featureAnnotation = MacheteFeature::class,
        internalDependencies = internalDependencies,
        recursive = true
    )

private fun Symbol.TypeSymbol.asFeature(
    featureAnnotation: KClass<*>,
    internalDependencies: List<Symbol.TypeSymbol>,
    recursive: Boolean,
    deepRecursive: Boolean = false
): Feature {
    val annotationMirror = annotationMirrors.find {
        it.type.asElement().qualifiedName.toString() == featureAnnotation.qualifiedName
    } ?: throw ClassIsNotAnnotatedException(
        this.qualifiedName.toString(),
        featureAnnotation.qualifiedName.toString()
    )

    var modulesParam: List<Attribute.Class>? = null
    var featuresParam: List<Attribute.Class>? = null
    var dependenciesParam: List<Attribute.Class>? = null

    annotationMirror.values.forEach {
        val name = it.fst.simpleName.toString()
        val value = it.snd.value
        when (name) {
            "modules" -> modulesParam = value as? List<Attribute.Class>
            "features" -> featuresParam = value as? List<Attribute.Class>
            "dependencies" -> dependenciesParam = value as? List<Attribute.Class>
        }
    }

    val features = if (recursive) {
        featuresParam.toTypeSymbols().map {
            it.asFeature(
                featureAnnotation = MacheteFeature::class,
                internalDependencies = emptyList(),
                recursive = deepRecursive,
                deepRecursive = deepRecursive
            )
        }
            .toSet()
    } else {
        emptySet()
    }

    val modules = if (recursive) {
        modulesParam.toTypeSymbols().map { it.asModule(emptyList()) }.toSet()
    } else {
        emptySet()
    }

    return Feature(
        coreClass = this,
        modules = modules,
        features = features,
        dependencies = dependenciesParam.toTypeSymbols().toList(),
        internalDependencies = internalDependencies
    )
}
