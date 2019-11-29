package dm.uporov.machete.apt

import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.exception.ClassIsNotAnnotatedException
import kotlin.reflect.KClass

fun Symbol.TypeSymbol.asFeature(featureAnnotation: KClass<*> = MacheteFeature::class): Feature {
    val annotationMirror = annotationMirrors.find {
        it.type.asElement().qualifiedName.toString() == featureAnnotation.qualifiedName
    } ?: throw ClassIsNotAnnotatedException(
        this.qualifiedName.toString(),
        featureAnnotation.qualifiedName.toString()
    )

    // TODO includeFeatures
//    var includeFeaturesParam: List<Attribute.Class>? = null
    var childFeaturesParam: List<Attribute.Class>? = null
    var dependenciesParam: List<Attribute.Class>? = null

    annotationMirror.values.forEach {
        val name = it.fst.simpleName.toString()
        val value = it.snd.value
        when (name) {
            // TODO includeFeatures
//    "includeFeatures" -> includeFeaturesParam = value as? List<Attribute.Class>
            "childFeatures" -> childFeaturesParam = value as? List<Attribute.Class>
            "dependencies" -> dependenciesParam = value as? List<Attribute.Class>
        }
    }

    return Feature(
        coreClass = this,
        // TODO includeFeatures
//    val includeFeatures = includeFeaturesParam.toTypeSymbolsSet()
        // TODO можно внутрь рекурсии передавать set фич - цепочку от корневого. Если наткнулись на уже имеющийся в set - кидаем исключение - зацикленные зависимости
        childFeatures = childFeaturesParam.toTypeSymbolsSet().map { it.asFeature() }.toSet(),
        dependencies = dependenciesParam.toTypeSymbolsSet()
    )
}

private fun List<Attribute.Class>?.toTypeSymbolsSet(): Set<Symbol.TypeSymbol> {
    this ?: return emptySet()

    return this.asSequence()
        .map(Attribute.Class::classType)
        .map(Type::asElement)
        .toSet()
}