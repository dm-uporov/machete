package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.generated.CoreAnalyticsModuleDependencies
import dm.uporov.app.App

class CoreAnalyticsModuleResolver(private val app: App) : CoreAnalyticsModuleDependencies {

    override fun getContext(): Context {
        return app.getContext()
    }
}