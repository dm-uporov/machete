package dm.uporov.machete.apt.model

import com.squareup.kotlinpoet.TypeName

data class FeatureMirror(
    val coreClass: TypeName,
    val includesFeatures: List<String>,
    val childFeatures: List<String>,
    val dependencies: List<Dependency>
)

fun FeatureMirror.reifie(
    includesFeatures: List<LegacyFeature>,
    childFeatures: List<LegacyFeature>
) = LegacyFeature(
    coreClass = coreClass,
    includesFeatures = includesFeatures,
    childFeatures = childFeatures,
    dependencies = dependencies
)

data class LegacyFeature(
    val coreClass: TypeName,
    val includesFeatures: List<LegacyFeature>,
    val childFeatures: List<LegacyFeature>,
    val dependencies: List<Dependency>
)
