package dm.uporov.machete

import android.app.Application
import android.content.Context
import dm.uporov.list.ListActivity
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.CoreAnalytics.coreAnalyticsFeatureDefinition
import dm.uporov.list.listActivityComponentDefinition
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.generated.AppComponent.Companion.appComponent
import dm.uporov.machete.generated.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.machete.generated.Machete.startMachete
import dm.uporov.machete.generated.injectAnalytics
import dm.uporov.machete.provider.just

@MacheteApplication(
    includeFeatures = [CoreAnalytics::class],
    childFeatures = [ListActivity::class],
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
            appComponent(
                appComponentDefinition(
                    coreAnalyticsFeatureDefinition,
                    contextProvider = just { this }
                ),
                appFromCoreAnalyticsProvider = just { this },
                appFromListActivityProvider = just { this }
            ),
            listActivityComponentDefinition
        )
    }
}