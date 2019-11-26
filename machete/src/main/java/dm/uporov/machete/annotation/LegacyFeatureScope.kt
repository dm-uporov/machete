package dm.uporov.machete.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class LegacyFeatureScope(
    val scopeId: Int,
    val isSinglePerScope: Boolean = true
)