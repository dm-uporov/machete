package dm.uporov.machete.di

import dm.uporov.analytics.Analytics
import dm.uporov.list.ListActivity
import dm.uporov.list.di.ListActivityComponentDependencies
import dm.uporov.machete.App
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

private lateinit var appComponentInstance: AppComponent

fun setAppComponentInstance(instance: AppComponent) {
    appComponentInstance = instance
}

private fun getAppComponent(): AppComponent {
    if (!::appComponentInstance.isInitialized) throw
    dm.uporov.machete.exception.MacheteIsNotInitializedException()

    return appComponentInstance
}

fun App.getAnalytics(): Analytics {
    return getAppComponent()
        .definition
        .analyticsProvider(this)
}

fun App.injectResources(): Lazy<Analytics> = lazy {
    getAppComponent()
        .definition
        .analyticsProvider(this)
}

class AppComponent private constructor(
    val definition: AppComponentDefinition,
    appFromListActivityProvider: Provider<ListActivity, App>
) : ListActivityComponentDependencies by ListActivityComponentResolver(
    definition,
    appFromListActivityProvider
) {

    private class ListActivityComponentResolver(
        private val definition: AppComponentDefinition,
        private val appFromListActivityProvider: Provider<ListActivity, App>
    ) : ListActivityComponentDependencies {

        override val analyticsProvider: Provider<ListActivity, Analytics>
            get() = definition.analyticsProvider.mapOwner(appFromListActivityProvider)
    }

    companion object {
        fun App.appComponent(
            definition: AppComponentDefinition,
            appFromListActivityProvider: Provider<ListActivity, App>
        ) = AppComponent(
            definition = definition,
            appFromListActivityProvider = appFromListActivityProvider
        )
    }
}

class AppComponentDefinition private constructor(
    val analyticsProvider: Provider<App, Analytics>
) {
    companion object {
        fun App.macheteAppComponentDefinition(
            analyticsProvider: Provider<App, Analytics>
        ) = AppComponentDefinition(analyticsProvider)
    }
}