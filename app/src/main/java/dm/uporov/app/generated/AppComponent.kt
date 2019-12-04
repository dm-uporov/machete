package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.CoreAnalyticsModuleDependencies
import dm.uporov.app.App
import dm.uporov.app.AppComponentDefinition
import dm.uporov.app.getAnalytics
import dm.uporov.list.ListActivity
import dm.uporov.list.ListActivityComponent.Companion.listActivityComponent
import dm.uporov.list.ListActivityComponentDependencies
import dm.uporov.list.setListActivityComponent
import dm.uporov.machete.exception.MacheteIsNotInitializedException
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
    val appFromListActivityProvider: Provider<ListActivity, App>
) {
    companion object {
        fun appComponent(
            definition: AppComponentDefinition
        ): AppComponent {
            val appComponent = AppComponent(
                contextProvider = definition.contextProvider, appFromListActivityProvider =
                definition.appFromListActivityProvider, analyticsProvider =
                definition.coreAnalyticsModuleDefinition.analyticsProvider.mapOwner(just {
                    CoreAnalyticsModuleDependenciesResolver(it)
                })
            )
            setListActivityComponent(
                listActivityComponent(
                    definition.listActivityComponentDefinition,
                    ListActivityComponentDependenciesResolver(appComponent)
                )
            )
            return appComponent

        }
    }

    private class CoreAnalyticsModuleDependenciesResolver(val app: App) :
        CoreAnalyticsModuleDependencies {
        override fun getContext(): Context {
            return app.getContext()
        }

        override fun getAnalytics(): Analytics {
            return app.getAnalytics()
        }
    }

    class ListActivityComponentDependenciesResolver(val appComponent: AppComponent) :
        ListActivityComponentDependencies {
        override val analyticsProvider: Provider<ListActivity, Analytics>
            get() = appComponent
                .analyticsProvider
                .mapOwner(
                    appComponent.appFromListActivityProvider
                )
    }
}
