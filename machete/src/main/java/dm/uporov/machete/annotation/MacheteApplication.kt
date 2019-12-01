package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к классу наследнику от Application.
 *
 * @param modules модули скоупа Application. Зависимости данных модулей распространяются на всё приложение
 *
 * @param features фичи верхнего уровня, от которых не зависит ни одна другая фича.
 *
 * @param dependencies зависимости скоупа Application, к классам которых нет непосредственного
 * доступа (например, Context). Такие зависимости необходимо будет зарезолвить при инициализации Machete.
 * Для всех остальных зависимостей стоит использовать @ApplicationScope для автоматического резолва.
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class MacheteApplication(
    val modules: Array<KClass<*>> = [],
    val features: Array<KClass<*>> = [],
    val dependencies: Array<KClass<*>> = []
)
