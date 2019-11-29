package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу - "входной точке" фичи.
 *
 * @param includesFeatures встроенные фичи, от которых зависит данная фича.
 * Указание здесь какой-либо фичи означает, что мы обязуемся зарезолвить необходимые для нее зависимости.
 * Если какая-либо зависимость резолвится уровнем выше, необходимо явно прописать её в dependencies.
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
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class MacheteFeature(
    val includesFeatures: Array<String> = [],
    val childFeatures: Array<String> = [],
    val dependencies: Array<KClass<*>> = []
)
