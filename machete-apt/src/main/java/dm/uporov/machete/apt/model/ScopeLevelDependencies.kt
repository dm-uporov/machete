package dm.uporov.machete.apt.model

data class ScopeLevelDependencies(
    val withProviders: Map<Int, Set<Dependency>>,
    val withoutProviders: Map<Int, Set<Dependency>>
)