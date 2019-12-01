package dm.uporov.analytics.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.machete.provider.Provider


class CoreAnalyticsComponentDefinition<T> private constructor(
    val analyticsProvider: Provider<CoreAnalyticsComponentDependencies<T>, Analytics>
) {
    companion object {
        fun <T> T.coreAnalyticsComponentDefinition2(
            analyticsProvider: Provider<CoreAnalyticsComponentDependencies<T>, Analytics>
        ): CoreAnalyticsComponentDefinition<T> = CoreAnalyticsComponentDefinition(
            analyticsProvider = analyticsProvider
        )
    }
}

interface CoreAnalyticsComponentDependencies<T> {

    fun getContext(owner: T): Context

    fun getAnalytics(owner: T): Analytics
}
