package dm.uporov.machete.apt.model

import com.squareup.kotlinpoet.TypeName

data class ApplicationMirror(
    val applicationClass: TypeName,
    val includesFeatures: List<String>,
    val childFeatures: List<String>,
    val dependencies: List<Dependency>
)

data class Application(
    val applicationClass: TypeName,
    val includesFeatures: List<FeatureMirror>,
    val childFeatures: List<FeatureMirror>,
    val dependencies: List<Dependency>
)



