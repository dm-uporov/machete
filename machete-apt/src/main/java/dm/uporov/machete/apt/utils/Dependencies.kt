package dm.uporov.machete.apt.utils

import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.machete.annotation.ModuleScope
import dm.uporov.machete.apt.model.Dependency
import dm.uporov.machete.exception.ClassIsNotAnnotatedException
import kotlin.reflect.KClass

fun Symbol.TypeSymbol.asFeatureScopeDependency() = asScopeDependency(
    scopeAnnotation = FeatureScope::class,
    featureFieldName = "feature"
)

fun Symbol.TypeSymbol.asModuleScopeDependency() = asScopeDependency(
    scopeAnnotation = ModuleScope::class,
    featureFieldName = "module"
)

private fun Symbol.TypeSymbol.asScopeDependency(
    scopeAnnotation: KClass<*>,
    featureFieldName: String
): Dependency {
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
            featureFieldName -> {
                return Dependency(
                    dependencyClass = this,
                    featureClass = (value as Type.ClassType).asElement()
                )
            }
        }
    }
    throw RuntimeException()
}
