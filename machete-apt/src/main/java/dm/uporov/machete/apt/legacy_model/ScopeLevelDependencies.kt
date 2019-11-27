package dm.uporov.machete.apt.legacy_model

data class ScopeLevelDependencies(
    val withProviders: Map<Int, Set<DependencyLegacy>>,
    val withoutProviders: Map<Int, Set<DependencyLegacy>>
)