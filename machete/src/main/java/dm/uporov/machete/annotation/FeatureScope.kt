package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Если аннотация применяется к конструктору класса, этот конструктор будет использоваться
 * для создания экземпляра класса в рамках скоупа указанной фичи.
 *
 * Если аннотация применяется к классу, необходимо указать явный провайдер в definition фичи.
 *
 * @param featureName строковый идентификтор фичи
 *
 * @param provideAs (optional) классы или интерфейсы, к которым биндится в рамках скоупа экземпляр класса.
 * Если здесь указан хотя бы один класс или интерфейс, зависимость не будет предоставлена по своему
 * реальному типу, для этого необходимо явно указать собственный тип.
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class FeatureScope(
    val featureName: String,
    val provideAs: Array<KClass<*>> = []
)