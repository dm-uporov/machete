package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу - "входной точке" фичи.
 *
 * @param featureName уникальный строковый идентификтор фичи
 *
 * @param includeFeatures фичи, от которых зависит (включает в себя) данная фича.
 * Предполагается, что весь скоуп таких фич доступен в рамках данной фичи.
 *
 * @param childFeatures дочерние фичи, не входящие в скоуп данной фичи.
 * Указание здесь какой-либо фичи означает, что мы обязуемся зарезолвить необходимые для нее зависимости.
 * Если какая-либо зависимость резолвится уровнем выше, необходимо явно прописать её в dependencies.
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
    val includeFeatures: Array<String> = [],
    val childFeatures: Array<String> = [],
    val dependencies: Array<KClass<*>> = []
)
