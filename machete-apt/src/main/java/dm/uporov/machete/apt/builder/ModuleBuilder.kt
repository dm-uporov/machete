package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.apt.utils.flatGenerics
import dm.uporov.machete.apt.utils.toClassName
import dm.uporov.machete.provider.Provider
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

private const val MODULE_CLASS_NAME_FORMAT = "%s_Module"
private const val MODULE_DEPENDENCIES_CLASS_NAME_FORMAT = "%s_ModuleDependencies"
private const val GETTER_NAME_FORMAT = "get%s"

private fun String.asModuleClassName() = MODULE_CLASS_NAME_FORMAT.format(this)
private fun String.asModuleDependenciesClassName() =
    MODULE_DEPENDENCIES_CLASS_NAME_FORMAT.format(this)

private fun String.asGetterName() = GETTER_NAME_FORMAT.format(this)

class ModuleBuilder(
    private val module: Module
) {

    private val coreClassName = module.coreClass.toClassName()
    private val coreClassPackage = coreClassName.packageName
    private val coreClassSimpleName = coreClassName.simpleName
    private val moduleName = coreClassSimpleName.asModuleClassName()
    private val moduleClassName = ClassName.bestGuess("$coreClassPackage.$moduleName")
    private val typeT = TypeVariableName("T")

    fun build(): FileSpec {
        return FileSpec.builder(coreClassPackage, moduleName)
            .addImport(coreClassPackage, coreClassSimpleName)
            .addImport("dm.uporov.machete.provider", "single", "factory")
            .addImport("dm.uporov.machete.exception", "MacheteIsNotInitializedException")
            .withModuleDependenciesInterface()
            .withModuleDefenition()
//            .withDependenciesField()
//            .withGetFunctions()
//            .withInjectFunctions()
            .build()
    }

    private fun FileSpec.Builder.withDependenciesField() = apply {
        addProperty(
            PropertySpec.builder(
                "dependencies",
                moduleClassName,
                KModifier.PRIVATE, KModifier.LATEINIT
            )
                .mutable(true)
                .build()
        )
        addFunction(
            FunSpec.builder("set$moduleName")
                .addParameter("dependenciesResolver", moduleClassName)
                .addStatement("dependencies = dependenciesResolver")
                .build()
        )
        addFunction(
            FunSpec.builder("getDependencies")
                .addModifiers(KModifier.PRIVATE)
                .returns(moduleClassName)
                .addStatement("if (!::dependencies.isInitialized) throw MacheteIsNotInitializedException()")
                .addStatement(" return dependencies")
                .build()
        )
    }

    private fun FileSpec.Builder.withGetFunctions() = apply {
        module.dependencies.forEach {
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
        module.dependencies.forEach {
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

    private fun FileSpec.Builder.withModuleDependenciesInterface() = apply {
        addType(
            TypeSpec.interfaceBuilder(coreClassSimpleName.asModuleDependenciesClassName())
                .addTypeVariable(typeT)
                .withDependenciesGetters()
                .build()
        )
    }

    private fun TypeSpec.Builder.withDependenciesGetters() = apply {
        module.dependencies.forEach {
            val type = it.asType().asTypeName()
            val uniqueName = type.flatGenerics()
            addFunction(
                FunSpec.builder(uniqueName.asGetterName())
                    .addParameter("scopeCore", typeT)
                    .addModifiers(KModifier.ABSTRACT)
                    .returns(type)
                    .build()
            )
        }
    }

    private fun FileSpec.Builder.withModuleDefenition() = apply {
        addType(
            TypeSpec.classBuilder(moduleClassName)
                .addTypeVariable(typeT)
                .withProvidersProperties()
//                .withComponentDefinitionCompanion()
                .build()
        )
    }


    private fun TypeSpec.Builder.withProvidersProperties() = apply {
        val constructorBuilder = FunSpec.constructorBuilder()
            .addModifiers(KModifier.PRIVATE)
        module.dependencies.forEach {
            val uniqueName = it.asType().asTypeName().flatGenerics()
            val providerName = uniqueName.asProviderName()
            val providerType = Provider::class.asClassName().parameterizedBy(
                typeT,
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
}
