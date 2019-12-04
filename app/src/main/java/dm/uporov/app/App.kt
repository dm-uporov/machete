package dm.uporov.app

import android.app.Application
import android.content.Context
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.coreAnalyticsModuleDefinition
import dm.uporov.app.App_ComponentDefinition.Companion.app_ComponentDefinition
import dm.uporov.app.generated.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.app.generated.Machete.startMachete
import dm.uporov.app.generated.injectAnalytics
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
        startMachete(
            app_ComponentDefinition(
                contextProvider = just { this },
                appFromListActivityProvider = just { this }
            ),
            listActivityComponentDefinition,
            coreAnalyticsModuleDefinition
        )
    }
}