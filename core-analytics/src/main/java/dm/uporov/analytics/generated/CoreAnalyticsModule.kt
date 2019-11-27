package dm.uporov.analytics.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.machete.provider.Provider

class CoreAnalyticsComponentDefinition private constructor(
    val analyticsProvider: Provider<CoreAnalyticsComponentDependencies, Analytics>
) {
    companion object {
        fun coreAnalyticsFeatureDefinition(
            analyticsProvider: Provider<CoreAnalyticsComponentDependencies, Analytics>
        ) = CoreAnalyticsComponentDefinition(
            analyticsProvider = analyticsProvider
        )
    }
}

interface CoreAnalyticsComponentDependencies {

    fun getContext(coreAnalytics: CoreAnalytics): Context

}