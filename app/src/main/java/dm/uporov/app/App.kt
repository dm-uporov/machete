package dm.uporov.app

import android.app.Application
import android.content.Context
import dm.uporov.app.AppComponentDefinition.Companion.appComponentDefinition
import dm.uporov.core.analytics.CoreAnalytics
import dm.uporov.core.analytics.api.Event
import dm.uporov.core.analytics.coreAnalyticsModule
import dm.uporov.feature.favorites.FavoritesActivity
import dm.uporov.feature.favorites.favoritesActivityComponent
import dm.uporov.feature.home.HomeActivity
import dm.uporov.feature.home.homeActivityComponent
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.extensions.applicationAsParentProvider
import dm.uporov.machete.provider.just

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
        analytics.sendEvent(Event("Applications is started"))
    }

    private fun initDi() {
        Machete.startMachete(
            appComponentDefinition(
                contextProvider = just { this },
                homeActivityParentProvider = applicationAsParentProvider(),
                favoritesActivityParentProvider = applicationAsParentProvider(),
                homeActivityComponentDefinition = homeActivityComponent,
                favoritesActivityComponentDefinition = favoritesActivityComponent,
                coreAnalyticsModuleDefinition = coreAnalyticsModule
            )
        )
    }
}