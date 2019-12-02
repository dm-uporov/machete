package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.generated.CoreAnalyticsComponentDefinition.Companion.coreAnalyticsComponentDefinition2
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    dependencies = [Context::class]
)
object CoreAnalytics

val coreAnalyticsComponent = coreAnalyticsComponentDefinition2(
    single { AnalyticsImpl(it.getContext()) }
)