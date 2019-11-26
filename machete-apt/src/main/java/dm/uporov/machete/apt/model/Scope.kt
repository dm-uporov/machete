package dm.uporov.machete.apt.model

import com.squareup.kotlinpoet.ClassName

data class Scope(
    val core: ClassName,
    val providedDependencies: Set<Dependency>
)