package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.generated.CoreAnalyticsComponentDefinition.Companion.coreAnalyticsComponentDefinition2
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.provider.single

@MacheteFeature(
    dependencies = [Context::class]
)
object CoreAnalytics

fun <T> T.coreAnalyticsComponent() = coreAnalyticsComponentDefinition2(
    single { AnalyticsImpl(it.getContext(this)) }
)