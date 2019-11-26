package dm.uporov.machete.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ApplicationScope(
    val isSinglePerScope: Boolean = true
)