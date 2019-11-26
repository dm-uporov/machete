package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Аннотация применяется к конструкторам класса.
 * Помеченные конструкторы будут использоваться для создания экземпляра класса в рамках
 * скоупа определенной фичи.
 *
 * @param featureName строковый идентификтор фичи
 *
 * @param bindings (optional) интерфейсы, к которым биндится в рамках скоупа экземпляр класса
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CONSTRUCTOR)
annotation class FeatureScope(
    val featureName: Int,
    vararg val bindings: KClass<*> = []
)