package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dm.uporov.machete.ModuleDependencies
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.apt.utils.flatGenerics
import dm.uporov.machete.apt.utils.toClassName
import dm.uporov.machete.provider.Provider

internal class ModuleBuilder(
    private val module: Module
) {

    private val coreClassName = module.coreClass.toClassName()
    private val coreClassPackage = coreClassName.packageName
    private val coreClassSimpleName = coreClassName.simpleName

    private val moduleDependenciesName = coreClassSimpleName.asModuleDependenciesClassName()
    private val moduleDependenciesClassName =
        ClassName.bestGuess("$coreClassPackage.$moduleDependenciesName")
    private val moduleDefinitionName = coreClassSimpleName.asModuleDefinitionClassName()
    private val moduleDefinitionClassName =
        ClassName.bestGuess("$coreClassPackage.$moduleDefinitionName")

    fun build(): FileSpec {
        return FileSpec.builder(coreClassPackage, coreClassSimpleName.asModuleClassName())
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .addImport("dm.uporov.machete", "ModuleDependencies")
            .withModuleDependenciesInterface()
            .withModuleDefinition()
            .build()
    }

    private fun FileSpec.Builder.withModuleDependenciesInterface() = apply {
        addType(
            TypeSpec.interfaceBuilder(moduleDependenciesName)
                .addSuperinterface(ModuleDependencies::class.asTypeName())
                .withDependenciesGetters()
                .build()
        )
    }

    private fun TypeSpec.Builder.withDependenciesGetters() = apply {
        (module.required + module.api)
            .forEach {
                val type = it.asType().asTypeName()
                val uniqueName = type.flatGenerics()
                addFunction(
                    FunSpec.builder(uniqueName.asGetterName())
                        .addModifiers(KModifier.ABSTRACT)
                        .returns(type)
                        .build()
                )
            }
    }

    private fun FileSpec.Builder.withModuleDefinition() = apply {
        addType(
            TypeSpec.classBuilder(moduleDefinitionClassName)
                .withProvidersProperties()
                .withModuleDefinitionCompanion()
                .build()
        )
    }

    private fun TypeSpec.Builder.withProvidersProperties() = apply {
        val constructorBuilder = FunSpec.constructorBuilder()
            .addModifiers(KModifier.PRIVATE)
        module.scopeDependencies
            .forEach {
                val uniqueName = it.asType().asTypeName().flatGenerics()
                val providerName = uniqueName.asProviderName()
                val providerType = Provider::class.asClassName().parameterizedBy(
                    moduleDependenciesClassName,
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

    private fun TypeSpec.Builder.withModuleDefinitionCompanion() = apply {
        addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder(moduleDefinitionName.decapitalize())
                        .returns(moduleDefinitionClassName)
                        .withProvidersParams()
                        .addStatement(
                            """
                            return $moduleDefinitionName(
                            ${module.scopeDependencies.joinToString {
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
        module.scopeDependencies
            .forEach {
                val uniqueName = it.asType().asTypeName().flatGenerics()
                val providerName = uniqueName.asProviderName()
                val providerType = Provider::class.asClassName().parameterizedBy(
                    moduleDependenciesClassName,
                    it.toClassName()
                )
                addParameter(
                    ParameterSpec.builder(providerName, providerType)
                        .build()
                )
            }
    }
}
