package dm.uporov.analytics.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.machete.provider.Provider

interface CoreAnalyticsModuleDependencies {
    fun getContext(): Context

    fun getAnalytics(): Analytics
}

class CoreAnalyticsModuleDefinition private constructor(val analyticsProvider:
        Provider<CoreAnalyticsModuleDependencies, Analytics>) {
    companion object {
        fun coreAnalyticsModuleDefinition(analyticsProvider:
                Provider<CoreAnalyticsModuleDependencies, Analytics>): CoreAnalyticsModuleDefinition
                = CoreAnalyticsModuleDefinition(
        analyticsProvider = analyticsProvider
        )
    }
}
