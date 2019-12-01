package dm.uporov.machete.apt.utils

import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.model.ScopeDependency
import dm.uporov.machete.exception.ClassIsNotAnnotatedException
import kotlin.reflect.KClass

fun Symbol.TypeSymbol.asFeature(featureAnnotation: KClass<*> = MacheteFeature::class): Feature {
    val annotationMirror = annotationMirrors.find {
        it.type.asElement().qualifiedName.toString() == featureAnnotation.qualifiedName
    } ?: throw ClassIsNotAnnotatedException(
        this.qualifiedName.toString(),
        featureAnnotation.qualifiedName.toString()
    )

    var includeFeaturesParam: List<Attribute.Class>? = null
    var childFeaturesParam: List<Attribute.Class>? = null
    var dependenciesParam: List<Attribute.Class>? = null

    annotationMirror.values.forEach {
        val name = it.fst.simpleName.toString()
        val value = it.snd.value
        when (name) {
            "includeFeatures" -> includeFeaturesParam = value as? List<Attribute.Class>
            "childFeatures" -> childFeaturesParam = value as? List<Attribute.Class>
            "dependencies" -> dependenciesParam = value as? List<Attribute.Class>
        }
    }

    return Feature(
        coreClass = this,
        // TODO можно внутрь рекурсии передавать список фич - цепочку от корневого.
        //  Если наткнулись на уже имеющийся в списке, кидаем исключение "зацикленные зависимости"
        includeFeatures = includeFeaturesParam.toTypeSymbols().map { it.asFeature() }.toSet(),
        // TODO -- // --
        childFeatures = childFeaturesParam.toTypeSymbols().map { it.asFeature() }.toSet(),
        dependencies = dependenciesParam.toTypeSymbols().toList()
    )
}

private fun List<Attribute.Class>?.toTypeSymbols(): Sequence<Symbol.TypeSymbol> {
    this ?: return emptySequence()

    return this.asSequence().map { it.classType.asElement() }
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
