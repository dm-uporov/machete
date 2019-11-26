package dm.uporov.machete.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class MacheteApplication(
    val dependencies: Array<KClass<*>>
)
