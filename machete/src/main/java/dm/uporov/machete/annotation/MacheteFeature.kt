package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу - "входной точке" фичи.
 *
 * @param featureName уникальный строковый идентификтор фичи
 *
 * @param includesFeatures фичи, от которых зависит данная фича. Предполагается, что весь скоуп таких
 * фич доступен в рамках данной фичи.
 *
 * @param dependencies внешние зависимости, необходимые для работы данной фичи.
 * Резолвятся либо фичами, которые зависят от данной фичи (данная фича входит в их includesFeatures),
 * либо вместе с Application dependencies при инициализации Machete.
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class MacheteFeature(
    val featureName: String,
    val includesFeatures: Array<String>,
    vararg val dependencies: KClass<*>
)
