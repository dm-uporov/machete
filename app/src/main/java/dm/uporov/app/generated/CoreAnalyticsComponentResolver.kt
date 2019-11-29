package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.generated.CoreAnalyticsComponentDependencies
import dm.uporov.app.App
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

class CoreAnalyticsComponentResolver(
    private val definition: AppComponentDefinition,
    private val appFromCoreAnalyticsProvider: Provider<CoreAnalytics, App>
) : CoreAnalyticsComponentDependencies {

    override fun getContext(coreAnalytics: CoreAnalytics): Context {
        return definition
            .contextProvider
            .mapOwner(appFromCoreAnalyticsProvider)
            .invoke(coreAnalytics)
    }
}