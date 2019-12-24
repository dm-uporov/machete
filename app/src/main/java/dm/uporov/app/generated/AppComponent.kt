package dm.uporov.app.generated

import android.content.Context
import com.example.core_analytics_api.Analytics
import dm.uporov.analytics.CoreAnalyticsModuleDependencies
import dm.uporov.app.App
import dm.uporov.app.AppComponentDependencies
import dm.uporov.feature_favorites.FavoritesActivity
import dm.uporov.feature_favorites.FavoritesActivityComponentDependencies
import dm.uporov.feature_home.HomeActivity
import dm.uporov.feature_home.HomeActivityComponentDependencies
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.ParentProvider
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.mapOwner

private lateinit var instance: AppComponent

fun setAppComponent(component: AppComponent) {
    instance = component
}

private fun getComponent(): AppComponent {
    if (!::instance.isInitialized) throw MacheteIsNotInitializedException()
    return instance
}

fun App.getContext(): Context {
    return getComponent().contextProvider.invoke(this)
}

fun App.getAnalytics(): Analytics {
    return getComponent().analyticsProvider.invoke(this)
}

fun App.injectContext(): Lazy<Context> {
    return lazy { getComponent().contextProvider.invoke(this) }
}

fun App.injectAnalytics(): Lazy<Analytics> {
    return lazy { getComponent().analyticsProvider.invoke(this) }
}

class AppComponent private constructor(
    val contextProvider: Provider<App, Context>,
    val analyticsProvider: Provider<App, Analytics>,
    val appFromHomeActivityProvider: ParentProvider<HomeActivity, App>,
    val appFromFavoritesActivityProvider: ParentProvider<FavoritesActivity, App>
) {
    companion object {
        fun appComponent(
            definition: AppComponentDefinition,
            dependencies: AppComponentDependencies
        ):
                AppComponent {
            val appComponent = AppComponent(

                contextProvider = definition.contextProvider,
                appFromHomeActivityProvider = definition.appFromHomeActivityProvider,
                appFromFavoritesActivityProvider = definition.appFromFavoritesActivityProvider,
                analyticsProvider =
                definition.coreAnalyticsModuleDefinition.analyticsProvider.mapOwner(just {
                    CoreAnalyticsModuleDependenciesResolver(definition, it)
                })
            )
            dm.uporov.feature_home.setHomeActivityComponent(
                dm.uporov.feature_home.HomeActivityComponent.homeActivityComponent(
                    definition.homeActivityComponentDefinition,
                    HomeActivityComponentDependenciesResolver(
                        appComponent
                    )
                )
            )
            dm.uporov.feature_favorites.setFavoritesActivityComponent(
                dm.uporov.feature_favorites.FavoritesActivityComponent.favoritesActivityComponent(
                    definition.favoritesActivityComponentDefinition,
                    FavoritesActivityComponentDependenciesResolver(
                        appComponent
                    )
                )
            )
            return appComponent
        }
    }
}

class CoreAnalyticsModuleDependenciesResolver(
    private val definition: AppComponentDefinition,
    private val app: App
) : CoreAnalyticsModuleDependencies {
    override fun getContext(): Context {
        return definition.contextProvider.invoke(app)
    }

    override fun getAnalytics(): Analytics = definition
        .coreAnalyticsModuleDefinition
        .analyticsProvider
        .invoke(this)
}

class HomeActivityComponentDependenciesResolver(
    private val appComponent: AppComponent
) : HomeActivityComponentDependencies {
    override val analyticsProvider: Provider<HomeActivity, Analytics>
        get() = appComponent
            .analyticsProvider
            .mapOwner(
                appComponent.appFromHomeActivityProvider
            )
}

class FavoritesActivityComponentDependenciesResolver(
    private val appComponent: AppComponent
) : FavoritesActivityComponentDependencies {
    override val analyticsProvider: Provider<FavoritesActivity, Analytics>
        get() = appComponent
            .analyticsProvider
            .mapOwner(
                appComponent.appFromFavoritesActivityProvider
            )
}
