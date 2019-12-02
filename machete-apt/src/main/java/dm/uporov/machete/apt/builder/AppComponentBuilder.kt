package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.utils.flatGenerics
import dm.uporov.machete.apt.utils.toClassName
import dm.uporov.machete.provider.Provider
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

private const val MACHETE_COMPONENT_DEPENDENCIES_CLASS_NAME_FORMAT = "%s_ComponentDependencies"
private const val MACHETE_COMPONENT_DEFINITION_CLASS_NAME_FORMAT = "%s_ComponentDefinition"

private fun String.asComponentDependenciesClassName() =
    MACHETE_COMPONENT_DEPENDENCIES_CLASS_NAME_FORMAT.format(this)

private fun String.asComponentDefinitionClassName() =
    MACHETE_COMPONENT_DEFINITION_CLASS_NAME_FORMAT.format(this)

internal class AppComponentBuilder(
    private val feature: Feature
) {

    private val coreClassName = feature.coreClass.toClassName()
    private val coreClassPackage = coreClassName.packageName
    private val coreClassSimpleName = coreClassName.simpleName
    private val componentDependenciesName = coreClassSimpleName.asComponentDependenciesClassName()
    private val componentDependenciesClassName =
        ClassName.bestGuess("$coreClassPackage.$componentDependenciesName")
    private val componentDefinitionName = coreClassSimpleName.asComponentDefinitionClassName()
    private val componentDefinitionClassName =
        ClassName.bestGuess("$coreClassPackage.$componentDefinitionName")


    fun build(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDependenciesName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withComponentDependenciesInterface()
            .withDependenciesField()
            .withDefinitionField()
            .withDependenciesGetFunctions()
            .withDependenciesInjectFunctions()
            .withInternalDependenciesGetFunctions()
            .withInternalDependenciesInjectFunctions()
            .withComponentDefinition()
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

    private fun FileSpec.Builder.withDefinitionField() = apply {
        addProperty(
            PropertySpec.builder(
                "definition",
                componentDefinitionClassName,
                KModifier.PRIVATE, KModifier.LATEINIT
            )
                .mutable(true)
                .build()
        )
        addFunction(
            FunSpec.builder("set$componentDefinitionName")
                .addParameter("instance", componentDefinitionClassName)
                .addStatement("definition = instance")
                .build()
        )
        addFunction(
            FunSpec.builder("getDefinition")
                .addModifiers(KModifier.PRIVATE)
                .returns(componentDefinitionClassName)
                .addStatement("if (!::definition.isInitialized) throw MacheteIsNotInitializedException()")
                .addStatement(" return definition")
                .build()
        )
    }

    private fun FileSpec.Builder.withDependenciesGetFunctions() = apply {
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

    private fun FileSpec.Builder.withDependenciesInjectFunctions() = apply {
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

    private fun FileSpec.Builder.withInternalDependenciesGetFunctions() = apply {
        feature.internalDependencies.forEach {
            val dependencyType = it.asType().asTypeName()
            val uniqueName = dependencyType.flatGenerics()
            addFunction(
                FunSpec.builder("get${uniqueName.capitalize()}")
                    .receiver(coreClassName)
                    .returns(dependencyType)
                    .addStatement(" return getDefinition().${uniqueName.asProviderName()}.invoke(this)")
                    .build()
            )
        }
    }

    private fun FileSpec.Builder.withInternalDependenciesInjectFunctions() = apply {
        feature.internalDependencies.forEach {
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
                    .addStatement(" return lazy { getDefinition().${uniqueName.asProviderName()}.invoke(this) }")
                    .build()
            )
        }
    }

    private fun FileSpec.Builder.withComponentDependenciesInterface() = apply {
        addType(
            TypeSpec.interfaceBuilder(componentDependenciesClassName)
                .withDependenciesProvidersProperties()
                .build()
        )
    }

    private fun TypeSpec.Builder.withDependenciesProvidersProperties() = apply {
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

    private fun FileSpec.Builder.withComponentDefinition() = apply {
        addType(
            TypeSpec.classBuilder(componentDefinitionClassName)
                .withDefinitionsProvidersProperties()
                .withComponentDefinitionCompanion()
                .build()
        )
    }

    private fun TypeSpec.Builder.withDefinitionsProvidersProperties() = apply {
        val constructorBuilder = FunSpec.constructorBuilder()
            .addModifiers(KModifier.PRIVATE)
        feature.internalDependencies.forEach {
            val uniqueName = it.asType().asTypeName().flatGenerics()
            val providerName = uniqueName.asProviderName()
            val providerType = Provider::class.asClassName().parameterizedBy(
                coreClassName,
                it.toClassName()
            )
            constructorBuilder.addParameter(
                ParameterSpec.builder(providerName, providerType)
                    .build()
            )
            addProperty(
                PropertySpec.builder(providerName, providerType)
                    .initializer(providerName)
                    .build()
            )
        }
        primaryConstructor(constructorBuilder.build())
    }

    private fun TypeSpec.Builder.withComponentDefinitionCompanion() = apply {
        addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder(componentDefinitionName.decapitalize())
                        .returns(componentDefinitionClassName)
                        .withProvidersParams()
                        .addStatement(
                            """
                            return $componentDefinitionName(
                            ${feature.internalDependencies.joinToString {
                                val providerName =
                                    it.asType().asTypeName().flatGenerics().asProviderName()
                                return@joinToString "$providerName = $providerName"
                            }}
                            )
                            """.trimIndent()
                        )
                        .build()
                )
                .build()
        )
    }

    private fun FunSpec.Builder.withProvidersParams() = apply {
        feature.internalDependencies.forEach {
            val uniqueName = it.asType().asTypeName().flatGenerics()
            val providerName = uniqueName.asProviderName()
            val providerType = Provider::class.asClassName().parameterizedBy(
                coreClassName,
                it.toClassName()
            )
            addParameter(
                ParameterSpec.builder(providerName, providerType)
                    .build()
            )
        }
    }
}