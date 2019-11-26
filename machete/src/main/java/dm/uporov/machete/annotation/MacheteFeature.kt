package dm.uporov.machete.annotation

import dm.uporov.machete.APPLICATION_SCOPE_ID

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class MacheteFeature(
    val scopeId: Int,
    val parentScopeId: Int = APPLICATION_SCOPE_ID
)
