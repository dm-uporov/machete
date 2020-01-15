package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу - "входной точке" модуля.
 * Класс используется для именования и связывания других зависимостей скоупа.
 * Для простоты можно использовать пустой interface или object.
 *
 * @param modules встроенные модули, от которых зависит данный модуль.
 * Указание здесь какого-либо модуля означает, что мы обязуемся зарезолвить необходимые для него зависимости.
 * Если какая-либо зависимость резолвится уровнем выше, необходимо явно прописать её в [required].
 *
 * @param api зависимости, предоставляемые модулем
 *
 * @param implementation зависимости, задействованные внутри модуля
 *
 * @param required внешние зависимости, необходимые для работы данного модуля.
 * Резолвятся фичами / модулями, которые используют данный модуль.
 *
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class MacheteModule(
    val modules: Array<KClass<*>> = [],
    val api: Array<KClass<*>> = [],
    val implementation: Array<KClass<*>> = [],
    val required: Array<KClass<*>> = []
)
