package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.CoreAnalytics.ANALYTICS_FEATURE
import dm.uporov.analytics.generated.CoreAnalyticsComponentDefinition.Companion.coreAnalyticsFeatureDefinition
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.provider.single

@MacheteFeature(
    name = ANALYTICS_FEATURE,
    dependencies = [Context::class]
)
object CoreAnalytics {

    const val ANALYTICS_FEATURE = "analytics"

    val coreAnalyticsFeatureDefinition = coreAnalyticsFeatureDefinition(
        single { AnalyticsImpl(it.getContext(this)) }
    )

}