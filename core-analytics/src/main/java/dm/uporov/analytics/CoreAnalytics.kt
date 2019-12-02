package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.generated.CoreAnalyticsModuleDefinition.Companion.coreAnalyticsComponentDefinition2
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    provide = [Analytics::class],
    dependencies = [Context::class]
)
object CoreAnalytics

val coreAnalyticsComponentDefinition = coreAnalyticsComponentDefinition2(
    single { AnalyticsImpl(it.getContext()) }
)