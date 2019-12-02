package dm.uporov.machete.apt

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.annotation.ApplicationScope
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.apt.builder.FeatureComponentBuilder
import dm.uporov.machete.apt.utils.asApplicationFeature
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(MacheteApplicationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MacheteApplicationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(MacheteApplication::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(
        set: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        val app = roundEnvironment
            .getElementsAnnotatedWith(MacheteApplication::class.java)
            .filterIsInstance<Symbol.ClassSymbol>()
            .firstOrNull() ?: return true

        val scopeDependencies =
            roundEnvironment.getElementsAnnotatedWith(ApplicationScope::class.java)
                .asSequence()
                .filterIsInstance<Symbol.TypeSymbol>()
                .toList()

        val appFeature = app.asApplicationFeature(scopeDependencies)
        FeatureComponentBuilder(appFeature).build().write()

        return true
    }

    private fun FileSpec.write() =
        writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]))

    override fun hashCode(): Int {
        return 1
    }
}
