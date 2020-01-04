package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу - "входной точке" фичи.
 *
 * @param modules встроенные модули, от которых зависит данная фича.
 * Указание здесь какого-либо модуля означает, что мы обязуемся зарезолвить необходимые для него зависимости.
 * Если какая-либо зависимость резолвится уровнем выше, необходимо явно прописать её в dependencies.
 *
 * @param features дочерние фичи, не входящие в скоуп данной фичи.
 * Указание здесь какой-либо фичи означает, что мы обязуемся зарезолвить необходимые для нее зависимости.
 * Если какая-либо зависимость резолвится уровнем выше, необходимо явно прописать её в dependencies.
 *
 * @param implementation зависимости, задействованные внутри фичи
 *
 * @param required внешние зависимости, необходимые для работы данной фичи.
 * Резолвятся фичами, использующими данную фичу.
 *
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class MacheteFeature(
    val modules: Array<KClass<*>> = [],
    val features: Array<KClass<*>> = [],
    val implementation: Array<KClass<*>> = [],
    val required: Array<KClass<*>> = []
)
