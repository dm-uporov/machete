package dm.uporov.app

import android.app.Application
import android.content.Context
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.coreAnalyticsModuleDefinition
import dm.uporov.app.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.list.ListActivity
import dm.uporov.list.listActivityComponentDefinition
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.provider.just

@MacheteApplication(
    modules = [CoreAnalytics::class],
    features = [ListActivity::class],
    dependencies = [Context::class]
)
class App : Application() {

    private val analytics by injectAnalytics()

    override fun onCreate() {
        super.onCreate()
        initDi()
        analytics.sendEvent("Machete works!")
    }

    private fun initDi() {
        Machete.startMachete(
            appComponentDefinition(
                contextProvider = just { this },
                appFromListActivityProvider = just { this },
                listActivityComponentDefinition = listActivityComponentDefinition,
                coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition
            )
        )
    }
}