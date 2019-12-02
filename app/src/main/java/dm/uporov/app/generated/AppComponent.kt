package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.generated.CoreAnalyticsComponentDefinition
import dm.uporov.analytics.generated.CoreAnalyticsComponentDependencies
import dm.uporov.app.App
import dm.uporov.list.ListActivity
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.mapOwner

private lateinit var appComponentInstance: AppComponent

fun setAppComponentInstance(instance: AppComponent) {
    appComponentInstance = instance
}

private fun getAppComponent(): AppComponent {
    if (!::appComponentInstance.isInitialized) throw
    MacheteIsNotInitializedException()

    return appComponentInstance
}

fun App.getContext(): Context {
    return getAppComponent()
        .contextProvider
        .invoke(this)
}

fun App.injectContext(): Lazy<Context> = lazy {
    getAppComponent()
        .contextProvider
        .invoke(this)
}

fun App.getAnalytics(): Analytics {
    return getAppComponent()
        .analyticsProvider
        .invoke(this)
}

fun App.injectAnalytics(): Lazy<Analytics> = lazy {
    getAppComponent()
        .analyticsProvider
        .invoke(this)
}

class AppComponent private constructor(
    val analyticsProvider: Provider<App, Analytics>,
    val contextProvider: Provider<App, Context>,
    val appFromListActivityProvider: Provider<ListActivity, App>
) {
    companion object {
        fun App.appComponent(
            coreAnalyticsComponentDefinition: CoreAnalyticsComponentDefinition,
            contextProvider: Provider<App, Context>,
            appFromListActivityProvider: Provider<ListActivity, App>
        ) = AppComponent(
            analyticsProvider = coreAnalyticsComponentDefinition.analyticsProvider.mapOwner(just { Resolver(it) }),
            contextProvider = contextProvider,
            appFromListActivityProvider = appFromListActivityProvider
        )
    }
}

class Resolver(private val app: App) : CoreAnalyticsComponentDependencies {
    override fun getContext(): Context {
        return app.getContext()
    }

    override fun getAnalytics(): Analytics {
        return app.getAnalytics()
    }

}