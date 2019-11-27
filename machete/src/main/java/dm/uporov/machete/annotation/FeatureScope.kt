package dm.uporov.machete.annotation

/**
 *
 * Если аннотация применяется к конструктору класса, этот конструктор будет использоваться
 * для создания экземпляра класса в рамках скоупа указанной фичи.
 *
 * Если аннотация применяется к классу, необходимо указать явный провайдер в definition фичи.
 *
 * @param featureName строковый идентификтор фичи
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class FeatureScope(
    val featureName: String
)