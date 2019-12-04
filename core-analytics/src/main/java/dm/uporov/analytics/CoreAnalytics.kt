package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.CoreAnalytics_ModuleDefinition.Companion.coreAnalytics_ModuleDefinition
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    dependencies = [Context::class],
    provide = [Analytics::class]
)
object CoreAnalytics

val coreAnalyticsModuleDefinition = coreAnalytics_ModuleDefinition(
    single { AnalyticsImpl(it.getContext()) }
)