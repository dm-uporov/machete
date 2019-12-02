package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.CoreAnalytics_ModuleDependencies
import dm.uporov.app.App

class CoreAnalyticsModuleResolver(private val app: App) : CoreAnalytics_ModuleDependencies {

    override fun getContext(): Context {
        return app.getContext()
    }
}