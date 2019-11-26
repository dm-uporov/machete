package dm.uporov.machete.apt.model

data class DependencyInfo(
    val scopeId: Int,
    val isSinglePerScope: Boolean = true
)