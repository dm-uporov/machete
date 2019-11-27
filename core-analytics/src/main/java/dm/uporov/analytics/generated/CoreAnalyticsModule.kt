package dm.uporov.analytics.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.machete.provider.Provider

class CoreAnalyticsModuleDefinition private constructor(
    val analyticsProvider: Provider<CoreAnalyticsModuleDependencies, Analytics>
) {
    companion object {
        fun coreAnalyticsModuleDefinition(
            analyticsProvider: Provider<CoreAnalyticsModuleDependencies, Analytics>
        ) = CoreAnalyticsModuleDefinition(
            analyticsProvider = analyticsProvider
        )
    }
}

interface CoreAnalyticsModuleDependencies {

    fun getContext(coreAnalytics: CoreAnalytics): Context

}