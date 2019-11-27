package dm.uporov.machete.apt.legacy_model

import com.squareup.kotlinpoet.ClassName

data class Scope(
    val core: ClassName,
    val providedDependencies: Set<DependencyLegacy>
)