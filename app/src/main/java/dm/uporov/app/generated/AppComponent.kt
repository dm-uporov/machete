package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.CoreAnalytics_ComponentDefinition
import dm.uporov.list.ListActivity
import dm.uporov.app.App
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

private lateinit var appComponentInstance: AppComponentDefinition

fun setAppComponentInstance(instance: AppComponentDefinition) {
    appComponentInstance = instance
}

private fun getAppComponent(): AppComponentDefinition {
    if (!::appComponentInstance.isInitialized) throw
    MacheteIsNotInitializedException()

    return appComponentInstance
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
    val analyticsProvider: Provider<App, Analytics>,
    val contextProvider: Provider<App, Context>,
    val appFromListActivityProvider: Provider<ListActivity, App>
) {
    companion object {
        fun App.appComponentDefinition(
            coreAnalyticsComponentDefinition: CoreAnalytics_ComponentDefinition,
            contextProvider: Provider<App, Context>,
            coreAnalyticsProvider: Provider<App, CoreAnalytics>,
            appFromListActivityProvider: Provider<ListActivity, App>
        ) = AppComponentDefinition(
            analyticsProvider = coreAnalyticsComponentDefinition.analyticsProvider.mapOwner(coreAnalyticsProvider),
            contextProvider = contextProvider,
            appFromListActivityProvider = appFromListActivityProvider
        )
    }
}