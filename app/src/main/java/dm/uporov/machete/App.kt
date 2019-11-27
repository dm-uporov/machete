package dm.uporov.machete

import android.app.Application
import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.CoreAnalytics.ANALYTICS_MODULE
import dm.uporov.analytics.CoreAnalytics.coreAnalyticsModuleDefinition
import dm.uporov.analytics.generated.CoreAnalyticsModuleDefinition
import dm.uporov.list.LIST_FEATURE
import dm.uporov.list.listActivityComponentDefinition
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.generated.AppComponent.Companion.appComponent
import dm.uporov.machete.generated.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.machete.generated.Machete.startMachete
import dm.uporov.machete.generated.injectAnalytics
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.single

@MacheteApplication(
    modules = [ANALYTICS_MODULE],
    features = [LIST_FEATURE]
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
            appComponent(
                appComponentDefinition(
                    coreAnalyticsModuleDefinition,
                    contextProvider = just { this }
                ),
                appFromCoreAnalyticsProvider = just { this },
                appFromListActivityProvider = just { this }
            ),
            listActivityComponentDefinition
        )
    }
}