package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Если аннотация применяется к конструктору класса, этот конструктор будет использоваться
 * для создания экземпляра класса в рамках скоупа указанного модуля.
 *
 * Если аннотация применяется к классу, необходимо указать явный провайдер в definition модуля.
 *
 * @param moduleName строковый идентификтор модуля
 *
 * @param provideAs (optional) классы или интерфейсы, к которым биндится в рамках скоупа экземпляр класса.
 * Если здесь указан хотя бы один класс или интерфейс, зависимость не будет предоставлена по своему
 * реальному типу, для этого необходимо явно указать собственный тип.
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class ModuleScope(
    val moduleName: String,
    val provideAs: Array<KClass<*>> = []
)