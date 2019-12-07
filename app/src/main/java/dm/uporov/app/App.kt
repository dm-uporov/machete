package dm.uporov.app

import android.app.Application
import android.content.Context
import com.example.core_analytics_api.Event
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.coreAnalyticsModuleDefinition
import dm.uporov.app.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.feature_home.HomeActivity
import dm.uporov.feature_home.homeActivityComponentDefinition
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.provider.just

@MacheteApplication(
    modules = [CoreAnalytics::class],
    features = [HomeActivity::class],
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
                appFromHomeActivityProvider = just { this },
                homeActivityComponentDefinition = homeActivityComponentDefinition,
                coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition
            )
        )
    }
}