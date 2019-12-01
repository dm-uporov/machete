package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Если аннотация применяется к конструктору класса, этот конструктор будет использоваться
 * для создания экземпляра класса в рамках скоупа указанной фичи.
 *
 * Если аннотация применяется к классу, необходимо указать явный провайдер в definition модуля.
 *
 * @param module класс - "входная точка" модуля
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class ModuleScope(
    val module: KClass<*>
)