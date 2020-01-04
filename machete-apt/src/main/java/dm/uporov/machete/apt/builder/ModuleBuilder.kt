package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dm.uporov.machete.ModuleDependencies
import dm.uporov.machete.apt.model.ConstructorProperty
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.apt.model.asConstructorProperty
import dm.uporov.machete.apt.utils.asDefinition
import dm.uporov.machete.apt.utils.asDependenciesResolver
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
            .withModuleDependenciesAbstractClass()
            .withModuleDefinition()
            .build()
    }

    private fun FileSpec.Builder.withModuleDependenciesAbstractClass() = apply {
        addType(
            TypeSpec.classBuilder(moduleDependenciesName)
                .withConstructorWithProperties(
                    listOf(
                        ConstructorProperty(
                            FieldName.DEFINITION,
                            moduleDefinitionClassName,
                            KModifier.PRIVATE
                        )
                    )
                )
                .addModifiers(KModifier.ABSTRACT)
                .addSuperinterface(ModuleDependencies::class.asTypeName())
                .withRequiredDependenciesGetters()
                .withApiDependenciesGetters()
                .withModulesApiDependenciesGetters()
                .withModulesResolvers()
                .build()
        )
    }

    private fun TypeSpec.Builder.withRequiredDependenciesGetters() = apply {
        module.required
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

    private fun TypeSpec.Builder.withApiDependenciesGetters() = apply {
        module.api
            .forEach {
                val type = it.asType().asTypeName()
                val uniqueName = type.flatGenerics()
                addFunction(
                    FunSpec.builder(uniqueName.asGetterName())
                        .returns(type)
                        .addStatement(
                            """
                            return ${FieldName.DEFINITION}
                                .${uniqueName.asProviderName()}
                                .invoke(this)
                        """.trimIndent()
                        )
                        .build()
                )
            }
    }

    private fun TypeSpec.Builder.withModulesApiDependenciesGetters() = apply {
        module.modules
            .forEach { subModule ->
                subModule.api.forEach {
                    val type = it.asType().asTypeName()
                    val uniqueName = type.flatGenerics()
                    val getterName = uniqueName.asGetterName()

                    val resolver = subModule.asDependenciesResolver()

                    addFunction(
                        FunSpec.builder(getterName)
                            .returns(type)
                            .addStatement(
                                """
                            return ${resolver.first.capitalize()}(
                                ${FieldName.DEFINITION},
                                this,
                                ${FieldName.FEATURE_OWNER}
                            ).$getterName()
                        """.trimIndent()
                            )
                            .build()
                    )
                }
            }
    }

    private fun FileSpec.Builder.withModuleDefinition() = apply {
        val providers = module.scopeDependencies
            .map {
                val providerName = it.asType().asTypeName().flatGenerics().asProviderName()
                val providerType = Provider::class.asClassName().parameterizedBy(
                    moduleDependenciesClassName,
                    it.toClassName()
                )
                return@map ConstructorProperty(providerName, providerType)
            }

        val moduleDefinitions = module.modules
            .asSequence()
            .map(Module::asDefinition)
            .map(Pair<String, TypeName>::asConstructorProperty)

        val properties = providers + moduleDefinitions

        addType(
            TypeSpec.classBuilder(moduleDefinitionClassName)
                .withConstructorWithProperties(properties, KModifier.PRIVATE)
                .withSimpleInitialCompanion(
                    moduleDefinitionName,
                    moduleDefinitionClassName,
                    properties
                )
                .build()
        )
    }

    private fun TypeSpec.Builder.withModulesResolvers() = apply {
        module.modules.forEach {
            val uniqueName = it.coreClass.asType().asTypeName().flatGenerics()
            val dependenciesClassName = uniqueName.asModuleDependenciesClassName()
            val resolverClassName = dependenciesClassName.asResolverClassName()

            val moduleDefinitionName = it.coreClass.toClassName()
                .simpleName.asModuleDefinitionClassName().decapitalize()

            val resolverClass = ClassName.bestGuess("$coreClassPackage.$resolverClassName")

            addType(
                TypeSpec.classBuilder(resolverClass)
                    .addModifiers(KModifier.PRIVATE)
                    .superclass(ClassName.bestGuess("${it.coreClass.packge()}.$dependenciesClassName"))
                    .addSuperclassConstructorParameter("${FieldName.DEFINITION}.$moduleDefinitionName")
                    .withConstructorWithProperties(
                        listOf(
                            ConstructorProperty(
                                FieldName.DEFINITION,
                                moduleDefinitionClassName,
                                KModifier.PRIVATE
                            ),
                            ConstructorProperty(
                                FieldName.DEPENDENCIES,
                                moduleDependenciesClassName,
                                KModifier.PRIVATE
                            ),
                            ConstructorProperty(
                                FieldName.FEATURE_OWNER,
                                Any::class.asTypeName(),
                                KModifier.OVERRIDE
                            )
                        )
                    )
                    .apply {
                        it.required
                            .forEach { dependency ->
                                val type = dependency.asType().asTypeName()
                                val dependencyUniqueName = type.flatGenerics()
                                val getterName = dependencyUniqueName.asGetterName()
                                val providerName = dependencyUniqueName.asProviderName()

                                val statement = if (module.required.contains(dependency)) {
                                    """
                                        return ${FieldName.DEPENDENCIES}
                                            .$getterName()
                                    """.trimIndent()
                                } else {
                                    """
                                        return ${FieldName.DEFINITION}
                                            .$providerName
                                            .invoke(${FieldName.FEATURE_OWNER})
                                    """.trimIndent()
                                }

                                addFunction(
                                    FunSpec.builder(getterName)
                                        .addModifiers(KModifier.OVERRIDE)
                                        .returns(type)
                                        .addStatement(statement)
                                        .build()
                                )
                            }
                    }
                    .build()
            )
        }
    }

}
