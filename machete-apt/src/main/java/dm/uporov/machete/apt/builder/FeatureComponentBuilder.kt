package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.apt.builder.FieldName.COMPONENT
import dm.uporov.machete.apt.builder.FieldName.COMPONENTS_LIST
import dm.uporov.machete.apt.builder.FieldName.DEFINITION
import dm.uporov.machete.apt.builder.FieldName.DEPENDENCIES
import dm.uporov.machete.apt.builder.FieldName.FEATURE_OWNER
import dm.uporov.machete.apt.builder.FieldName.IS_RELEVANT_FOR
import dm.uporov.machete.apt.model.ConstructorProperty
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.apt.model.asConstructorProperty
import dm.uporov.machete.apt.utils.flatGenerics
import dm.uporov.machete.apt.utils.toClassName
import dm.uporov.machete.provider.ParentProvider
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

    private val isRelevantForLambdaType = LambdaTypeName.get(
        parameters = listOf(ParameterSpec.unnamed(coreClassName)),
        returnType = Boolean::class.asTypeName()
    )

    fun buildDependencies(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDependenciesName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .withDependenciesInterface()
            .build()
    }

    fun buildDefinition(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDefinitionName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .withDefinition()
            .build()
    }

    fun buildComponent(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory", "mapOwner", "just")
            .addImport("dm.uporov.machete.exception", "SubFeatureIsNotInitializedException")
            .withComponentsListField()
            .withProvideFunctions()
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
        val providers = feature.required.map(::dependencyProvider)
        val properties = providers.map { PropertySpec.builder(it.first, it.second).build() }

        addProperties(properties)
    }

    private fun FileSpec.Builder.withDefinition() = apply {
        val properties = feature.internalDependencies
            .asSequence()
            .plus(feature.modules.asSequence().map(Module::required).flatten())
            .plus(feature.features.asSequence().map(Feature::required).flatten())
            .minus(feature.required)
            .minus(feature.modules.asSequence().map(Module::api).flatten())
            .distinct()
            .map(::dependencyProvider)
            .plus(feature.features.asSequence().map(::featureParentProvider))
            .plus(feature.features.asSequence().map(::featureDefinition))
            .plus(feature.modules.asSequence().map(::moduleDefinition))
            .map(Pair<String, TypeName>::asConstructorProperty)
            .toList()

        addType(
            TypeSpec.classBuilder(componentDefinitionClassName)
                .withConstructorWithProperties(properties)
                .withSimpleInitialCompanion(
                    ownerName = componentDefinitionName,
                    returns = componentDefinitionClassName,
                    constructorProperties = properties
                )
                .build()
        )
    }

    private fun FileSpec.Builder.withComponentsListField() = apply {
        addProperty(
            PropertySpec.builder(
                COMPONENTS_LIST,
                ClassName("kotlin.collections", "MutableList")
                    .parameterizedBy(componentClassName),
                KModifier.PRIVATE
            )
                .mutable(true)
                .initializer("mutableListOf<$componentName>()")
                .build()
        )
        addFunction(
            FunSpec.builder(componentName.asSetterName())
                .addParameter(COMPONENT, componentClassName)
                .addStatement("$COMPONENTS_LIST.add($COMPONENT)")
                .build()
        )
        addFunction(
            FunSpec.builder("getComponent")
                .receiver(coreClassName)
                .addModifiers(KModifier.PRIVATE)
                .returns(componentClassName)
                .addStatement("val $COMPONENT = $COMPONENTS_LIST.find { it.$IS_RELEVANT_FOR(this) }")
                .addStatement("if ($COMPONENT == null) throw SubFeatureIsNotInitializedException(this::class)")
                .addStatement("return $COMPONENT")
                .build()
        )
    }

    private fun FileSpec.Builder.withProvideFunctions() = apply {
        feature.scopeDependencies.forEach {
            val dependencyType = it.asType().asTypeName()
            val uniqueName = dependencyType.flatGenerics()
            addFunction(
                FunSpec.builder("provide${uniqueName.capitalize()}")
                    .addModifiers(KModifier.INTERNAL)
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
                    .addModifiers(KModifier.INTERNAL)
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
                .withFeaturesResolvers()
                .withModulesResolvers()
                .build()
        )
    }

    private fun TypeSpec.Builder.withComponentProvidersProperties(): TypeSpec.Builder {
        val properties = feature.scopeDependencies
            .asSequence()
            .plus(feature.modules.map(Module::api).flatten())
            .plus(feature.features.map(Feature::required).flatten())
            .distinct()
            .map(::dependencyProvider)
            .plus(feature.features.map(::featureParentProvider))
            .plus(IS_RELEVANT_FOR to isRelevantForLambdaType)
            .map(Pair<String, TypeName>::asConstructorProperty)
            .toList()

        return withConstructorWithProperties(properties, KModifier.PRIVATE)
    }

    private fun TypeSpec.Builder.withComponentCompanion() = apply {
        addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder(componentName.decapitalize())
                        .addParameter(DEFINITION, componentDefinitionClassName)
                        .addParameter(DEPENDENCIES, componentDependenciesClassName)
                        .addParameter(IS_RELEVANT_FOR, isRelevantForLambdaType)
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
                                    .asSequence()
                                    .plus(feature.modules.asSequence().map(Module::required).flatten())
                                    .plus(feature.features.asSequence().map(Feature::required).flatten())
                                    .distinct()
                                    .minus(feature.required)
                                    .minus(feature.modules.asSequence().map(Module::api).flatten())
                                    .map {
                                        val providerName = it.providerName()
                                        "\n$providerName = $DEFINITION.$providerName"
                                    }
                                    .plus(feature.features
                                        .asSequence()
                                        .map(::featureParentProvider)
                                        .map {
                                            val name = it.first
                                            "\n$name = $DEFINITION.$name"
                                        })
                                    .plus(feature.required.asSequence().map {
                                        val providerName = it.providerName()
                                        "\n$providerName = $DEPENDENCIES.$providerName"
                                    })
                                    .plus(modulesWithDefinitionsNames.asSequence().map { (module, name) ->
                                        module.api.map { dependency ->
                                            val providerName = dependency.providerName()
                                            "\n$providerName = $DEFINITION.${name.decapitalize()}.$providerName.mapOwner(just { ${
                                            module.coreClass
                                                .asType()
                                                .asTypeName()
                                                .flatGenerics()
                                                .asModuleDependenciesClassName()
                                                .asResolverClassName()
                                            }($DEFINITION, $DEPENDENCIES, it) })"
                                        }.joinToString()
                                    })
                                    .plus("$IS_RELEVANT_FOR = $IS_RELEVANT_FOR")
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
                                        $featurePackage.$featureComponentName.${featureComponentName.decapitalize()}(
                                            $DEFINITION.${featureName.asComponentDefinitionClassName().decapitalize()},
                                            ${featureName.asComponentDependenciesClassName().asResolverClassName()}(
                                                ${componentName.decapitalize()}
                                            ),
                                            $DEFINITION.${featureParentProvider(it).first}.$IS_RELEVANT_FOR
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

    private fun TypeSpec.Builder.withFeaturesResolvers() = apply {
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
                        it.required.forEach { dependency ->
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
                                                $componentParameter.${uniqueName.parentProvider()}
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

    private fun TypeSpec.Builder.withModulesResolvers() = apply {
        feature.modules.forEach {
            val uniqueName = it.coreClass.asType().asTypeName().flatGenerics()
            val dependenciesClassName = uniqueName.asModuleDependenciesClassName()
            val resolverClassName = dependenciesClassName.asResolverClassName()

            val moduleDefinitionName = it.coreClass.toClassName()
                .simpleName.asModuleDefinitionClassName().decapitalize()

            addType(
                TypeSpec.classBuilder(ClassName.bestGuess("$coreClassPackage.$resolverClassName"))
                    .addModifiers(KModifier.PRIVATE)
                    .superclass(ClassName.bestGuess("${it.coreClass.packge()}.$dependenciesClassName"))
                    .addSuperclassConstructorParameter("$DEFINITION.$moduleDefinitionName")
                    .withConstructorWithProperties(
                        listOf(
                            ConstructorProperty(
                                DEFINITION,
                                componentDefinitionClassName,
                                KModifier.PRIVATE
                            ),
                            ConstructorProperty(
                                DEPENDENCIES,
                                componentDependenciesClassName,
                                KModifier.PRIVATE
                            ),
                            ConstructorProperty(
                                FEATURE_OWNER,
                                coreClassName,
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

                                val provider = if (feature.required.contains(dependency)) {
                                    DEPENDENCIES
                                } else {
                                    DEFINITION
                                }

                                addFunction(
                                    FunSpec.builder(getterName)
                                        .addModifiers(KModifier.OVERRIDE)
                                        .returns(type)
                                        .addStatement(" return $provider.$providerName.invoke($FEATURE_OWNER)")
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

    private fun featureParentProvider(child: Feature): Pair<String, TypeName> {
        val uniqueName = child.coreClass.asType().asTypeName().flatGenerics()

        val providerType = ParentProvider::class.asClassName().parameterizedBy(
            child.coreClass.toClassName(),
            coreClassName
        )
        return uniqueName.parentProvider() to providerType
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