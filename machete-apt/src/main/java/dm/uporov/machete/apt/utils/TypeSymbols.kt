package dm.uporov.machete.apt.utils

import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.apt.model.ScopeDependency
import dm.uporov.machete.exception.ClassIsNotAnnotatedException
import kotlin.reflect.KClass

fun Symbol.TypeSymbol.asApplicationFeature() = asFeature(MacheteApplication::class)

fun Symbol.TypeSymbol.asFeature() = asFeature(MacheteFeature::class)

private fun Symbol.TypeSymbol.asFeature(featureAnnotation: KClass<*>): Feature {
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

    return Feature(
        coreClass = this,
        // TODO не проваливаться на несколько уровней. Хватит информации просто из дочерних
        modules = modulesParam.toTypeSymbols().map { it.asModule() }.toSet(),
        // TODO не проваливаться на несколько уровней. Хватит информации просто из дочерних
        features = featuresParam.toTypeSymbols().map { it.asFeature() }.toSet(),
        dependencies = dependenciesParam.toTypeSymbols().toList()
    )
}

private fun List<Attribute.Class>?.toTypeSymbols(): Sequence<Symbol.TypeSymbol> {
    this ?: return emptySequence()

    return this.asSequence().map { it.classType.asElement() }
}

fun Symbol.TypeSymbol.asModule(): Module {
    val annotationMirror = annotationMirrors.find {
        it.type.asElement().qualifiedName.toString() == MacheteModule::class.qualifiedName
    } ?: throw ClassIsNotAnnotatedException(
        this.qualifiedName.toString(),
        MacheteModule::class.qualifiedName.toString()
    )

    var modulesParam: List<Attribute.Class>? = null
    var dependenciesParam: List<Attribute.Class>? = null
    var provideParam: List<Attribute.Class>? = null

    annotationMirror.values.forEach {
        val name = it.fst.simpleName.toString()
        val value = it.snd.value
        when (name) {
            "modules" -> modulesParam = value as? List<Attribute.Class>
            "dependencies" -> dependenciesParam = value as? List<Attribute.Class>
            "provide" -> provideParam = value as? List<Attribute.Class>
        }
    }

    return Module(
        coreClass = this,
        modules = modulesParam.toTypeSymbols().map { it.asModule() }.toSet(),
        provide = provideParam.toTypeSymbols().toList(),
        dependencies = dependenciesParam.toTypeSymbols().toList()
    )
}

fun Symbol.TypeSymbol.asScopeDependency(scopeAnnotation: KClass<*> = FeatureScope::class): ScopeDependency {
    val annotationMirror = annotationMirrors.find {
        it.type.asElement().qualifiedName.toString() == scopeAnnotation.qualifiedName
    } ?: throw ClassIsNotAnnotatedException(
        this.qualifiedName.toString(),
        scopeAnnotation.qualifiedName.toString()
    )

    annotationMirror.values.forEach {
        val name = it.fst.simpleName.toString()
        val value = it.snd.value
        when (name) {
            "feature" -> {
                return ScopeDependency(
                    dependencyClass = this,
                    featureClass = (value as Type.ClassType).asElement()
                )
            }
        }
    }
    throw RuntimeException()
}
