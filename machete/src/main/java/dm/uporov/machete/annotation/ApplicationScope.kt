package dm.uporov.machete.annotation

/**
 *
 * Если аннотация применяется к конструктору класса, этот конструктор будет использоваться
 * для создания экземпляра класса в рамках Application скоупа
 *
 * Если аннотация применяется к классу, необходимо указать явный провайдер в definition Application компонента.
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ApplicationScope