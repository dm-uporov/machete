package dm.uporov.machete.apt

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.sun.source.util.Trees
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.JCTree.JCClassDecl
import com.sun.tools.javac.tree.JCTree.JCExpression
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Names
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
            .forEach {
                val feature = it.asFeature(scopeDependencies[it] ?: emptyList())
                val builder = FeatureComponentBuilder(feature)

                builder.buildDependencies().write()
                builder.buildDefinition().write()
                builder.buildComponent().write()


                val typeSpec = TypeSpec.classBuilder("Middle")
                    .addModifiers(KModifier.OPEN)
                    .addFunction(FunSpec.builder("someFun").build())
                    .build()
                val file =
                    FileSpec.builder(it.enclosingElement.toString(), typeSpec.name.toString())
                        .addType(typeSpec)
                        .build()

                val trees = Trees.instance(processingEnv)
                val filer = processingEnv.filer

                file.write()

                val context = (processingEnv as JavacProcessingEnvironment).context
                val treeMaker = TreeMaker.instance(context)
                val names = Names.instance(context)

                var selector: JCExpression = treeMaker.Ident(names.fromString(file.packageName))
                selector = treeMaker.Select(selector, names.fromString(typeSpec.name)) as JCExpression
                (trees.getTree(it) as JCClassDecl).extending = selector
            }


        return true
    }

    private fun FileSpec.write() =
        writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]))

    override fun hashCode(): Int {
        return 1
    }
}
