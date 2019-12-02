package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.utils.flatGenerics
import dm.uporov.machete.apt.utils.toClassName
import dm.uporov.machete.provider.Provider
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

private const val MACHETE_COMPONENT_CLASS_NAME_FORMAT = "%s_Component"
private const val MACHETE_COMPONENT_DEPENDENCIES_CLASS_NAME_FORMAT = "%s_ComponentDependencies"
private const val MACHETE_COMPONENT_DEFINITION_CLASS_NAME_FORMAT = "%s_ComponentDefinition"

private fun String.asComponentClassName() =
    MACHETE_COMPONENT_CLASS_NAME_FORMAT.format(this)

private fun String.asComponentDependenciesClassName() =
    MACHETE_COMPONENT_DEPENDENCIES_CLASS_NAME_FORMAT.format(this)

private fun String.asComponentDefinitionClassName() =
    MACHETE_COMPONENT_DEFINITION_CLASS_NAME_FORMAT.format(this)

internal class FeatureComponentBuilder(
    private val feature: Feature
) {

    private val coreClassName = feature.coreClass.toClassName()
    private val coreClassPackage = coreClassName.packageName
    private val coreClassSimpleName = coreClassName.simpleName

    private val componentName = coreClassSimpleName.asComponentClassName()
    private val componentClassName =
        ClassName.bestGuess("$coreClassPackage.$componentName")
    private val componentDependenciesName = coreClassSimpleName.asComponentDependenciesClassName()
    private val componentDependenciesClassName =
        ClassName.bestGuess("$coreClassPackage.$componentDependenciesName")
    private val componentDefinitionName = coreClassSimpleName.asComponentDefinitionClassName()
    private val componentDefinitionClassName =
        ClassName.bestGuess("$coreClassPackage.$componentDefinitionName")

    fun buildDependencies(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDependenciesName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withComponentDependenciesInterface()
            .build()
    }

    fun buildDefinition(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDefinitionName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withComponentDefinition()
            .build()
    }

    fun buildComponent(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withComponentField()
            .withGetFunctions()
            .withInjectFunctions()
            .withComponent()
            .build()
    }

    private fun FileSpec.Builder.withComponentField() = apply {
        addProperty(
            PropertySpec.builder(
                "instance",
                componentClassName,
                KModifier.PRIVATE, KModifier.LATEINIT
            )
                .mutable(true)
                .build()
        )
        addFunction(
            FunSpec.builder("set$componentName")
                .addParameter("component", componentClassName)
                .addStatement("instance = component")
                .build()
        )
        addFunction(
            FunSpec.builder("getComponent")
                .addModifiers(KModifier.PRIVATE)
                .returns(componentClassName)
                .addStatement("if (!::instance.isInitialized) throw MacheteIsNotInitializedException()")
                .addStatement(" return instance")
                .build()
        )
    }

    private fun FileSpec.Builder.withGetFunctions() = apply {
        feature.scopeDependencies.forEach {
            val dependencyType = it.asType().asTypeName()
            val uniqueName = dependencyType.flatGenerics()
            addFunction(
                FunSpec.builder("get${uniqueName.capitalize()}")
                    .receiver(coreClassName)
                    .returns(dependencyType)
                    .addStatement(" return getComponent().${uniqueName.asProviderName()}.invoke(this)")
                    .build()
            )
        }
    }

    private fun FileSpec.Builder.withInjectFunctions() = apply {
        feature.scopeDependencies.forEach {
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
                    .addStatement(" return lazy { getComponent().${uniqueName.asProviderName()}.invoke(this) }")
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

    private fun FileSpec.Builder.withComponent() = apply {
        addType(
            TypeSpec.classBuilder(componentClassName)
                .withProvidersProperties()
                .withComponentCompanion()
                .build()
        )
    }

    private fun TypeSpec.Builder.withProvidersProperties() = apply {
        val constructorBuilder = FunSpec.constructorBuilder()
            .addModifiers(KModifier.PRIVATE)
        feature.scopeDependencies.forEach {
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

    private fun TypeSpec.Builder.withComponentCompanion() = apply {
        addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder(componentName.decapitalize())
                        // TODO receiver Machete
//                        .receiver()
                        .addParameter("definition", componentDefinitionClassName)
                        .addParameter("dependencies", componentDependenciesClassName)
                        .returns(componentClassName)
                        .addStatement(
                            """
                            return $componentName(
                            ${feature.internalDependencies
                                .map {
                                    val providerName = it.providerName()
                                    "$providerName = definition.$providerName"
                                }
                                .plus(feature.dependencies.map {
                                    val providerName = it.providerName()
                                    "$providerName = dependencies.$providerName"
                                })
                                .joinToString()}
                            )
                            """.trimIndent()
                        )
                        .build()
                )
                .build()
        )
    }

    private fun Symbol.providerName() = asType().asTypeName().flatGenerics().asProviderName()
}