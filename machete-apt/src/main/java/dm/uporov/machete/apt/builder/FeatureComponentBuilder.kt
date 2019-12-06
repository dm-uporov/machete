package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.apt.utils.flatGenerics
import dm.uporov.machete.apt.utils.toClassName
import dm.uporov.machete.provider.Provider
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName


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
            .withDependenciesInterface()
            .build()
    }

    fun buildDefinition(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDefinitionName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withDefinition()
            .build()
    }

    fun buildComponent(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory", "mapOwner", "just")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withComponentField()
            .withGetFunctions()
            .withInjectFunctions()
            .withComponent()
            .build()
    }


    private fun FileSpec.Builder.withDependenciesInterface() = apply {
        addType(
            TypeSpec.interfaceBuilder(componentDependenciesClassName)
                .withDependenciesProvidersProperties()
                .build()
        )
    }

    private fun TypeSpec.Builder.withDependenciesProvidersProperties() = apply {
        val providers = feature.dependencies.map(::dependencyProvider)
        val properties = providers.map { PropertySpec.builder(it.first, it.second).build() }

        addProperties(properties)
    }

    private fun FileSpec.Builder.withDefinition() = apply {
        val properties = feature.internalDependencies.map(::dependencyProvider)
            .plus(feature.features.map(::featureFromChildProvider))
            .plus(feature.features.map(::featureDefinition))
            .plus(feature.modules.map(::moduleDefinition))

        addType(
            TypeSpec.classBuilder(componentDefinitionClassName)
                .withConstructorWithProperties(properties)
                .withSimpleInitialCompanion(
                    ownerName = componentDefinitionName,
                    returns = componentDefinitionClassName,
                    propertiesNamesWithTypes = properties
                )
                .build()
        )
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
            FunSpec.builder(componentName.asSetterName())
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

    private fun FileSpec.Builder.withComponent() = apply {
        addType(
            TypeSpec.classBuilder(componentClassName)
                .withComponentProvidersProperties()
                .withComponentCompanion()
                .withResolvers()
                .build()
        )
    }

    private fun TypeSpec.Builder.withComponentProvidersProperties() = apply {
        val properties = feature.scopeDependencies
            .plus(feature.modules.map(Module::provideDependencies).flatten())
            .distinct()
            .map(::dependencyProvider)
            .plus(feature.features.map(::featureFromChildProvider))

        withConstructorWithProperties(
            properties,
            KModifier.PRIVATE
        )
    }

    private fun TypeSpec.Builder.withComponentCompanion() = apply {
        addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder(componentName.decapitalize())
                        .addParameter("definition", componentDefinitionClassName)
                        .addParameter("dependencies", componentDependenciesClassName)
                        .returns(componentClassName)
                        .apply {
                            val modulesWithDefinitionsNames = feature.modules.map {
                                val moduleClassName = it.coreClass.toClassName()
                                val definitionName =
                                    moduleClassName.simpleName.asModuleDefinitionClassName()
                                it to definitionName
                            }

                            addStatement(
                                """
                            val ${componentName.decapitalize()} = $componentName(
                            ${feature.internalDependencies
                                    .map {
                                        val providerName = it.providerName()
                                        "$providerName = definition.$providerName"
                                    }
                                    .plus(feature.features.map(::featureFromChildProvider).map {
                                        val name = it.first
                                        "$name = definition.$name"
                                    })
                                    .plus(feature.dependencies.map {
                                        val providerName = it.providerName()
                                        "$providerName = dependencies.$providerName"
                                    })
                                    .plus(modulesWithDefinitionsNames.map { (module, name) ->
                                        module.provideDependencies.map { dependency ->
                                            val providerName = dependency.providerName()
                                            "$providerName = definition.${name.decapitalize()}.$providerName.mapOwner(just { ${
                                            module.coreClass
                                                .toClassName()
                                                .simpleName
                                                .asModuleDependenciesClassName()
                                                .asResolverClassName()
                                            }(it) })"
                                        }.joinToString()
                                    })
                                    .joinToString()}
                            )
                            """.trimIndent()
                            )
                            feature.features.forEach {
                                val featureClass = it.coreClass.toClassName()
                                val featureName = featureClass.simpleName
                                val featureComponentName = featureName.asComponentClassName()
                                val featurePackage = featureClass.packageName
                                addStatement(
                                    """
                                    $featurePackage.${featureComponentName.asSetterName()}(
                                        $featurePackage.$featureComponentName.Companion.${featureComponentName.decapitalize()}(
                                            definition.${featureName.asComponentDefinitionClassName().decapitalize()},
                                            ${featureName.asComponentDependenciesClassName().asResolverClassName()}(
                                                ${componentName.decapitalize()}
                                            )
                                        )
                                    )
                                """.trimIndent()
                                )
                            }
                            addStatement("return ${componentName.decapitalize()}")
                        }
                        .build()
                )
                .build()
        )
    }

    private fun TypeSpec.Builder.withResolvers() = apply {
        feature.modules.forEach {
            val uniqueName = it.coreClass.asType().asTypeName().flatGenerics()
            val dependenciesClassName = uniqueName.asModuleDependenciesClassName()
            val resolverClassName = dependenciesClassName.asResolverClassName()

            val coreClassParameter = coreClassSimpleName.decapitalize()

            addType(
                TypeSpec.classBuilder(ClassName.bestGuess("$coreClassPackage.$resolverClassName"))
                    .addModifiers(KModifier.PRIVATE)
                    .addSuperinterface(ClassName.bestGuess("${it.coreClass.packge()}.$dependenciesClassName"))
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(coreClassParameter, coreClassName)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(coreClassParameter, coreClassName)
                            .initializer(coreClassParameter)
                            .build()
                    )
                    .apply {
                        (it.dependencies + it.provideDependencies)
                            .forEach { dependency ->
                                val type = dependency.asType().asTypeName()
                                val dependencyUniqueName = type.flatGenerics()
                                val getterName = dependencyUniqueName.asGetterName()
                                addFunction(
                                    FunSpec.builder(getterName)
                                        .addModifiers(KModifier.OVERRIDE)
                                        .returns(type)
                                        .addStatement(" return $coreClassParameter.$getterName()")
                                        .build()
                                )
                            }
                    }
                    .build()
            )
        }
        feature.features.forEach {
            val uniqueName = it.coreClass.asType().asTypeName().flatGenerics()
            val dependenciesClassName = uniqueName.asComponentDependenciesClassName()
            val resolverClassName = dependenciesClassName.asResolverClassName()

            val componentParameter = componentName.decapitalize()

            addType(
                TypeSpec.classBuilder(ClassName.bestGuess("$coreClassPackage.$resolverClassName"))
                    .addModifiers(KModifier.PRIVATE)
                    .addSuperinterface(ClassName.bestGuess("${it.coreClass.packge()}.$dependenciesClassName"))
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(componentParameter, componentClassName)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            componentParameter,
                            componentClassName,
                            KModifier.PRIVATE
                        )
                            .initializer(componentParameter)
                            .build()
                    )
                    .apply {
                        it.dependencies.forEach { dependency ->
                            val dependencyUniqueName =
                                dependency.asType().asTypeName().flatGenerics()
                            val providerName = dependencyUniqueName.asProviderName()
                            addProperty(
                                PropertySpec.builder(
                                    providerName,
                                    Provider::class.asClassName().parameterizedBy(
                                        it.coreClass.toClassName(),
                                        dependency.toClassName()
                                    ),
                                    KModifier.OVERRIDE
                                )
                                    .getter(
                                        FunSpec.getterBuilder()
                                            .addStatement(
                                                """
                                             return $componentParameter
                                             .$providerName
                                             .mapOwner(
                                                $componentParameter.${coreClassSimpleName.providerFrom(
                                                    uniqueName
                                                )}
                                             )
                                        """.trimIndent()
                                            )
                                            .build()
                                    )
                                    .build()
                            )
                        }
                    }
                    .build()
            )
        }
    }

    private fun dependencyProvider(dependency: Symbol.TypeSymbol): Pair<String, TypeName> {
        val uniqueName = dependency.asType().asTypeName().flatGenerics()
        val providerName = uniqueName.asProviderName()
        val providerType = Provider::class.asClassName().parameterizedBy(
            coreClassName,
            dependency.toClassName()
        )
        return providerName to providerType
    }

    private fun featureFromChildProvider(child: Feature): Pair<String, TypeName> {
        val uniqueName = child.coreClass.asType().asTypeName().flatGenerics()
        val providerName = coreClassSimpleName.providerFrom(uniqueName)

        val providerType = Provider::class.asClassName().parameterizedBy(
            child.coreClass.toClassName(),
            coreClassName
        )
        return providerName to providerType
    }

    private fun featureDefinition(child: Feature): Pair<String, TypeName> {
        val childClassName = child.coreClass.toClassName()
        val childName = childClassName.flatGenerics()

        val definitionName = childName.asComponentDefinitionClassName()
        val definitionClassName =
            ClassName.bestGuess("${childClassName.packageName}.$definitionName")
        return definitionName.decapitalize() to definitionClassName
    }

    private fun moduleDefinition(module: Module): Pair<String, TypeName> {
        val moduleClassName = module.coreClass.toClassName()
        val moduleName = moduleClassName.flatGenerics()

        val definitionName = moduleName.asModuleDefinitionClassName()
        val definitionClassName =
            ClassName.bestGuess("${moduleClassName.packageName}.$definitionName")
        return definitionName.decapitalize() to definitionClassName
    }
}