package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import dm.uporov.machete.apt.model.Feature
import dm.uporov.machete.apt.utils.toClassName

internal class MacheteBuilder(appFeature: Feature) {
    // core
    private val coreClassName = appFeature.coreClass.toClassName()
    private val corePackage = coreClassName.packageName
    private val coreName = coreClassName.simpleName
    // definition
    private val coreDefinitionName = coreName.asComponentDefinitionClassName()
    private val coreDefinitionClass = ClassName.bestGuess("$corePackage.$coreDefinitionName")

    private val coreComponentName = coreName.asComponentClassName()
    private val coreDependenciesName = coreName.asComponentDependenciesClassName()

    fun buildMachete(): FileSpec {
        return FileSpec.builder(corePackage, "Machete")
            .addType(
                TypeSpec.objectBuilder("Machete")
                    .addFunction(
                        FunSpec.builder("startMachete")
                            .addParameter(coreDefinitionName.decapitalize(), coreDefinitionClass)
                            .addStatement(
                                """
                                    $corePackage.${coreComponentName.asSetterName()}(
                                        $corePackage.$coreComponentName.${coreComponentName.decapitalize()}(
                                            ${coreDefinitionName.decapitalize()},
                                            object : $corePackage.$coreDependenciesName {},
                                            { true }
                                        )
                                    )
                                """.trimIndent()
                            )
                            .build()
                    )
                    .build()
            )
            .build()
    }
}