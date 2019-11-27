package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу - некоторому представлению модуля.
 *
 * @param includeModules модули, от которых зависит (включает в себя) данный модуль.
 * Предполагается, что весь скоуп таких модулей доступен в рамках данного модуля.
 *
 * @param dependencies внешние зависимости, необходимые для работы данного модуля.
 * Резолвятся либо фичами, которые подключают данный модуль (данная фича входит в их includesFeatures),
 * либо вместе с Application dependencies при инициализации Machete.
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class MacheteModule(
    val moduleName: String,
    val includeModules: Array<String> = [],
    val dependencies: Array<KClass<*>> = []
)
