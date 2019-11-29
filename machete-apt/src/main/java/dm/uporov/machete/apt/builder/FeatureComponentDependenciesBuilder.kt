package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import dm.uporov.machete.apt.flatGenerics
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.toClassName
import dm.uporov.machete.provider.Provider
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

private const val MACHETE_COMPONENT_DEPENDENCIES_CLASS_NAME_FORMAT = "%s_ComponentDependencies"
private const val PROVIDER_NAME_FORMAT = "%sProvider"

private fun String.asComponentDependenciesClassName() =
    MACHETE_COMPONENT_DEPENDENCIES_CLASS_NAME_FORMAT.format(this)

private fun String.asProviderName() = PROVIDER_NAME_FORMAT.format(this).decapitalize()

class FeatureComponentDependenciesBuilder(
    private val feature: Feature
) {

    private val coreClassName = feature.coreClass.toClassName()
    private val coreClassPackage = coreClassName.packageName
    private val coreClassSimpleName = coreClassName.simpleName
    private val componentDependenciesName = coreClassSimpleName.asComponentDependenciesClassName()
    private val componentDependenciesClassName =
        ClassName.bestGuess("$coreClassPackage.$componentDependenciesName")

    fun build(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDependenciesName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withDependenciesField()
            .withGetFunctions()
            .withInjectFunctions()
            .withComponentDependenciesInterface()
            .build()
    }

    private fun FileSpec.Builder.withDependenciesField() = apply {
        addProperty(
            PropertySpec.builder(
                "dependencies",
                componentDependenciesClassName,
                KModifier.PRIVATE, KModifier.LATEINIT
            )
                .mutable(true)
                .build()
        )
        addFunction(
            FunSpec.builder("set$componentDependenciesName")
                .addParameter("dependenciesResolver", componentDependenciesClassName)
                .addStatement("dependencies = dependenciesResolver")
                .build()
        )
        addFunction(
            FunSpec.builder("getDependencies")
                .addModifiers(KModifier.PRIVATE)
                .returns(componentDependenciesClassName)
                .addStatement("if (!::dependencies.isInitialized) throw MacheteIsNotInitializedException()")
                .addStatement(" return dependencies")
                .build()
        )
    }

    private fun FileSpec.Builder.withGetFunctions() = apply {
        feature.dependencies.forEach {
            val dependencyType = it.asType().asTypeName()
            val uniqueName = dependencyType.flatGenerics()
            addFunction(
                FunSpec.builder("get${uniqueName.capitalize()}")
                    .receiver(coreClassName)
                    .returns(dependencyType)
                    .addStatement(" return getDependencies().${uniqueName.asProviderName()}.invoke(this)")
                    .build()
            )
        }
    }

    private fun FileSpec.Builder.withInjectFunctions() = apply {
        feature.dependencies.forEach {
            val dependencyType = it.asType().asTypeName()
            val uniqueName = dependencyType.flatGenerics()

            val lazy =
                JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(dependencyType.toString()))
                    ?.asSingleFqName()
                    ?.asString()
                    ?.let {
                        Lazy::class.asClassName().parameterizedBy(ClassName.bestGuess(it))
                    } ?: Lazy::class.asClassName().parameterizedBy(dependencyType)

            addFunction(
                FunSpec.builder("inject${uniqueName.capitalize()}")
                    .receiver(coreClassName)
                    .returns(lazy)
                    .addStatement(" return lazy { getDependencies().${uniqueName.asProviderName()}.invoke(this) }")
                    .build()
            )
        }
    }

    private fun FileSpec.Builder.withComponentDependenciesInterface() = apply {
        addType(
            TypeSpec.interfaceBuilder(componentDependenciesClassName)
                .withProvidersProperties()
                .build()
        )
    }

    private fun TypeSpec.Builder.withProvidersProperties() = apply {
        feature.dependencies.forEach {
            val uniqueName = it.asType().asTypeName().flatGenerics()
            addProperty(
                PropertySpec.builder(
                    uniqueName.asProviderName(),
                    Provider::class.asClassName().parameterizedBy(
                        coreClassName,
                        it.toClassName()
                    )
                ).build()
            )
        }
    }
}