package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.CoreAnalytics_ModuleDefinition
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

class AppComponentDefinition private constructor(
    val contextProvider: Provider<App, Context>,
    val appFromListActivityProvider: Provider<ListActivity, App>
) {
    companion object {
        fun appComponentDefinition(
            contextProvider: Provider<App, Context>,
            appFromListActivityProvider: Provider<ListActivity, App>
        ) = AppComponentDefinition(
            contextProvider = contextProvider,
            appFromListActivityProvider = appFromListActivityProvider
        )
    }
}

class AppComponent private constructor(
    val analyticsProvider: Provider<App, Analytics>,
    val contextProvider: Provider<App, Context>,
    val appFromListActivityProvider: Provider<ListActivity, App>
) {
    companion object {
        fun Machete.appComponent(
            appComponentDefinition: AppComponentDefinition,
            coreAnalyticsComponentDefinition: CoreAnalytics_ModuleDefinition
        ) = AppComponent(
            analyticsProvider = coreAnalyticsComponentDefinition.analyticsProvider
                .mapOwner(just { CoreAnalyticsModuleResolver(it) }),
            contextProvider = appComponentDefinition.contextProvider,
            appFromListActivityProvider = appComponentDefinition.appFromListActivityProvider
        )
    }
}