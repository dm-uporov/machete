package dm.uporov.machete.apt

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.apt.builder.FeatureComponentBuilder
import dm.uporov.machete.apt.model.Dependency
import dm.uporov.machete.apt.utils.asFeature
import dm.uporov.machete.apt.utils.asFeatureScopeDependency
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(MacheteApplicationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MacheteFeaturesProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(MacheteFeature::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(
        set: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment
    ): Boolean {

        val scopeDependencies = roundEnvironment.getElementsAnnotatedWith(FeatureScope::class.java)
            .asSequence()
            .filterIsInstance<Symbol.TypeSymbol>()
            .map { it.asFeatureScopeDependency() }
            .groupBy { it.featureClass }
            .mapValues { it.value.map(Dependency::dependencyClass) }

        roundEnvironment.getElementsAnnotatedWith(MacheteFeature::class.java)
            .asSequence()
            .filterIsInstance<Symbol.TypeSymbol>()
            .map { it.asFeature(scopeDependencies[it] ?: emptyList()) }
            .map(::FeatureComponentBuilder)
            .forEach {
                it.buildDependencies().write()
                it.buildDefinition().write()
                it.buildComponent().write()
            }

        return true
    }

    private fun FileSpec.write() =
        writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]))

    override fun hashCode(): Int {
        return 1
    }
}
