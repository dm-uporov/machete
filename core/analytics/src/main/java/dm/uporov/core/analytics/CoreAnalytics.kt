package dm.uporov.core.analytics

import android.content.Context
import dm.uporov.core.analytics.api.Analytics
import dm.uporov.analytics.CoreAnalyticsModuleDefinition.Companion.coreAnalyticsModuleDefinition
import dm.uporov.core.analytics.impl.AnalyticsImpl
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    api = [Analytics::class],
    required = [Context::class]
)
object CoreAnalytics

val coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition(
    single { AnalyticsImpl(it.getContext()) }
)