package dm.uporov.machete.apt

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.annotation.ModuleScope
import dm.uporov.machete.apt.builder.ModuleBuilder
import dm.uporov.machete.apt.model.ScopeDependency
import dm.uporov.machete.apt.utils.asModule
import dm.uporov.machete.apt.utils.asModuleScopeDependency
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(MacheteDependenciesProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MacheteModulesProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(MacheteModule::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(
        set: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment
    ): Boolean {

        val scopeDependencies = roundEnvironment.getElementsAnnotatedWith(ModuleScope::class.java)
            .asSequence()
            .filterIsInstance<Symbol.TypeSymbol>()
            .map { it.asModuleScopeDependency() }
            .groupBy { it.featureClass }
            .mapValues { it.value.map(ScopeDependency::dependencyClass) }

        roundEnvironment.getElementsAnnotatedWith(MacheteModule::class.java)
            .asSequence()
            .filterIsInstance<Symbol.TypeSymbol>()
            .map { it.asModule(scopeDependencies[it] ?: emptyList()) }
            .forEach {
                ModuleBuilder(it).build().write()
            }

        return true
    }

    private fun FileSpec.write() =
        writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]))

    override fun hashCode(): Int {
        return 1
    }
}
