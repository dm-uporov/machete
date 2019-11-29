package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.generated.CoreAnalyticsComponentDefinition.Companion.coreAnalyticsFeatureDefinition
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.provider.single

@MacheteFeature(
    dependencies = [Context::class]
)
object CoreAnalytics {

    val coreAnalyticsFeatureDefinition = coreAnalyticsFeatureDefinition(
        single { AnalyticsImpl(it.getContext(this)) }
    )

}