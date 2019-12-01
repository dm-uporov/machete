package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.CoreAnalytics_ComponentDependencies
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

class CoreAnalyticsComponentResolver(
    private val definition: AppComponent
) : CoreAnalytics_ComponentDependencies {

    override val contextProvider: Provider<CoreAnalytics, Context>
        get() = definition
            .contextProvider
            .mapOwner(definition.appFromCoreAnalyticsProvider)
}