package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.apt.flatGenerics
import dm.uporov.machete.apt.toClassName
import dm.uporov.machete.provider.Provider
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

private const val MACHETE_COMPONENT_DEFINITION_CLASS_NAME_FORMAT = "%s_ComponentDefinition"
private const val PROVIDER_NAME_FORMAT = "%sProvider"

private fun String.asComponentDefinitionClassName() =
    MACHETE_COMPONENT_DEFINITION_CLASS_NAME_FORMAT.format(this)

private fun String.asProviderName() = PROVIDER_NAME_FORMAT.format(this).decapitalize()

class FeatureComponentDefinitionBuilder(
    private val feature: Symbol.TypeSymbol,
    private val dependencies: List<Symbol.TypeSymbol>
) {

    private val coreClassName = feature.toClassName()
    private val coreClassPackage = coreClassName.packageName
    private val coreClassSimpleName = coreClassName.simpleName
    private val componentDefinitionName = coreClassSimpleName.asComponentDefinitionClassName()
    private val componentDefinitionClassName =
        ClassName.bestGuess("$coreClassPackage.$componentDefinitionName")

    fun build(): FileSpec {
        return FileSpec.builder(coreClassPackage, componentDefinitionName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withDefinitionField()
            .withGetFunctions()
            .withInjectFunctions()
            .withComponentDefinition()
            .build()
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

    private fun FileSpec.Builder.withGetFunctions() = apply {
        dependencies.forEach {
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

    private fun FileSpec.Builder.withInjectFunctions() = apply {
        dependencies.forEach {
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

    private fun FileSpec.Builder.withComponentDefinition() = apply {
        addType(
            TypeSpec.classBuilder(componentDefinitionClassName)
                .withProvidersProperties()
                .withComponentDefinitionCompanion()
                .build()
        )
    }

    private fun TypeSpec.Builder.withProvidersProperties() = apply {
        val constructorBuilder = FunSpec.constructorBuilder()
            .addModifiers(KModifier.PRIVATE)
        dependencies.forEach {
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
                            ${dependencies.joinToString {
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
        dependencies.forEach {
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