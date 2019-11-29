package dm.uporov.machete.apt.model

import com.squareup.kotlinpoet.TypeName

data class ApplicationMirror(
    val applicationClass: TypeName,
    val includesFeatures: List<String>,
    val childFeatures: List<String>,
    val dependencies: List<Dependency>
)

fun ApplicationMirror.reifie(
    includesFeatures: List<LegacyFeature>,
    childFeatures: List<LegacyFeature>
) = Application(
    applicationClass = applicationClass,
    includesFeatures = includesFeatures,
    childFeatures = childFeatures,
    dependencies = dependencies
)


data class Application(
    val applicationClass: TypeName,
    val includesFeatures: List<LegacyFeature>,
    val childFeatures: List<LegacyFeature>,
    val dependencies: List<Dependency>
)



