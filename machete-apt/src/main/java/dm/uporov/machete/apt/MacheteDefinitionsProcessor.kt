package dm.uporov.machete.apt

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.annotation.ApplicationScope
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.machete.apt.builder.FeatureComponentDefinitionBuilder
import dm.uporov.machete.apt.model.ScopeDependency
import dm.uporov.machete.apt.utils.asScopeDependency
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(MacheteDefinitionsProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MacheteDefinitionsProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            ApplicationScope::class.java.name,
            FeatureScope::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(
        set: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        // TODO
//        roundEnvironment.getElementsAnnotatedWith(ApplicationScope::class.java)

        roundEnvironment.getElementsAnnotatedWith(FeatureScope::class.java)
            .asSequence()
            .filterIsInstance<Symbol.TypeSymbol>()
            .map { it.asScopeDependency() }
            .groupBy { it.featureClass }
            .mapValues { it.value.map(ScopeDependency::dependencyClass) }
            .forEach { (feature, dependencies) ->
                FeatureComponentDefinitionBuilder(feature, dependencies)
                    .build()
                    .write()
            }

        return true
    }

    private fun FileSpec.write() =
        writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]))

    override fun hashCode(): Int {
        return 1
    }
}
