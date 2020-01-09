package dm.uporov.app

import android.app.Application
import android.content.Context
import dm.uporov.core.analytics.api.Event
import dm.uporov.core.analytics.CoreAnalytics
import dm.uporov.core.analytics.coreAnalyticsModuleDefinition
import dm.uporov.app.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.feature.favorites.FavoritesActivity
import dm.uporov.feature.favorites.favoritesActivityComponentDefinition
import dm.uporov.feature.home.HomeActivity
import dm.uporov.feature.home.homeActivityComponentDefinition
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.parentProvider

@MacheteApplication(
    modules = [CoreAnalytics::class],
    features = [HomeActivity::class, FavoritesActivity::class],
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
                homeActivityParentProvider = parentProvider({ true }, just { this }),
                favoritesActivityParentProvider = parentProvider({ true }, just { this }),
                homeActivityComponentDefinition = homeActivityComponentDefinition,
                favoritesActivityComponentDefinition = favoritesActivityComponentDefinition,
                coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition
            )
        )
    }
}