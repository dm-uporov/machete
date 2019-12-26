package dm.uporov.analytics

import android.content.Context
import dm.uporov.core_analytics_api.Analytics
import dm.uporov.analytics.CoreAnalyticsModuleDefinition.Companion.coreAnalyticsModuleDefinition
import dm.uporov.core_analytics_impl.AnalyticsImpl
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    dependencies = [Context::class],
    provide = [Analytics::class]
)
object CoreAnalytics

val coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition(
    single { AnalyticsImpl(it.getContext()) }
)