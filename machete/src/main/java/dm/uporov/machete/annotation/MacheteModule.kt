package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу - "входной точке" модуля.
 *
 * @param modules встроенные модули, от которых зависит данный модуль.
 * Указание здесь какого-либо модуля означает, что мы обязуемся зарезолвить необходимые для него зависимости.
 * Если какая-либо зависимость резолвится уровнем выше, необходимо явно прописать её в dependencies.
 *
 * @param dependencies внешние зависимости, необходимые для работы данного модуля.
 * Резолвятся фичами / модулями, которые зависят от данного модуля.
 *
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class MacheteModule(
    val modules: Array<KClass<*>> = [],
    val dependencies: Array<KClass<*>> = []
)
