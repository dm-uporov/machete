package dm.uporov.analytics.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.machete.provider.Provider


class CoreAnalyticsComponentDefinition private constructor(
    val analyticsProvider: Provider<CoreAnalyticsComponentDependencies, Analytics>
) {
    companion object {
        fun coreAnalyticsComponentDefinition2(
            analyticsProvider: Provider<CoreAnalyticsComponentDependencies, Analytics>
        ): CoreAnalyticsComponentDefinition = CoreAnalyticsComponentDefinition(
            analyticsProvider = analyticsProvider
        )
    }
}

interface CoreAnalyticsComponentDependencies {

    fun getContext(): Context

    fun getAnalytics(): Analytics
}
