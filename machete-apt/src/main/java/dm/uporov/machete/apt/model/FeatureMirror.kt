package dm.uporov.machete.apt.model

import com.squareup.kotlinpoet.TypeName

data class FeatureMirror(
    val coreClass: TypeName,
    val name: String,
    val includesFeatures: List<String>,
    val childFeatures: List<String>,
    val dependencies: List<Dependency>
)

fun FeatureMirror.reifie(
    includesFeatures: List<Feature>,
    childFeatures: List<Feature>
) = Feature(
    coreClass = coreClass,
    name = name,
    includesFeatures = includesFeatures,
    childFeatures = childFeatures,
    dependencies = dependencies
)

data class Feature(
    val coreClass: TypeName,
    val name: String,
    val includesFeatures: List<Feature>,
    val childFeatures: List<Feature>,
    val dependencies: List<Dependency>
)



