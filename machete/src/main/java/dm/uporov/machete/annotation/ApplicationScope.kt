package dm.uporov.machete.annotation

import kotlin.reflect.KClass

/**
 *
 * Если аннотация применяется к конструктору класса, этот конструктор будет использоваться
 * для создания экземпляра класса в рамках Application скоупа
 *
 * Если аннотация применяется к классу, необходимо указать явный провайдер в definition Application компонента.
 *
 * @param isSinglePerScope используется при аннотации конструктора. Определяет, будет ли использоваться
 * единый экземпляр зависимости, или же необходима фабрика
 *
 * @param provideAs (optional) классы или интерфейсы, к которым биндится в рамках скоупа экземпляр класса.
 * Если здесь указан хотя бы один класс или интерфейс, зависимость не будет предоставлена по своему
 * реальному типу, для этого необходимо явно указать собственный тип.
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ApplicationScope(
    val isSinglePerScope: Boolean = true,
    val provideAs: Array<KClass<*>> = []
)