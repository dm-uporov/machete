package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.CoreAnalytics.ANALYTICS_MODULE
import dm.uporov.analytics.generated.CoreAnalyticsModuleDefinition.Companion.coreAnalyticsModuleDefinition
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    moduleName = ANALYTICS_MODULE,
    dependencies = [Context::class]
)
object CoreAnalytics {

    const val ANALYTICS_MODULE = "analytics"

    val coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition(
        single { AnalyticsImpl(it.getContext(this)) }
    )

}