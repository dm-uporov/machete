package dm.uporov.app

import android.app.Application
import android.content.Context
import com.example.core_analytics_api.Event
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.coreAnalyticsModuleDefinition
import dm.uporov.app.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.list.ListFragment
import dm.uporov.list.listFragmentComponentDefinition
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.provider.just

@MacheteApplication(
    modules = [CoreAnalytics::class],
    features = [ListFragment::class],
    dependencies = [Context::class]
)
class App : Application() {

    private val analytics by injectAnalytics()

    override fun onCreate() {
        super.onCreate()
        initDi()
        analytics.sendEvent(Event("Machete works!"))
    }

    private fun initDi() {
        Machete.startMachete(
            appComponentDefinition(
                contextProvider = just { this },
                appFromListFragmentProvider = just { this },
                listFragmentComponentDefinition = listFragmentComponentDefinition,
                coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition
            )
        )
    }
}