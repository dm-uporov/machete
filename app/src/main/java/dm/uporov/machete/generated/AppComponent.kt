package dm.uporov.machete.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.analytics.CoreAnalytics
import dm.uporov.analytics.generated.CoreAnalyticsComponentDefinition
import dm.uporov.analytics.generated.CoreAnalyticsComponentDependencies
import dm.uporov.list.ListActivity
import dm.uporov.list.generated.ListActivityComponentDependencies
import dm.uporov.machete.App
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

fun App.getAnalytics(): Analytics {
    return getAppComponent()
        .definition
        .analyticsProvider
        .invoke(this)
}

fun App.injectAnalytics(): Lazy<Analytics> = lazy {
    getAppComponent()
        .definition
        .analyticsProvider
        .invoke(this)
}

class AppComponent private constructor(
    val definition: AppComponentDefinition,
    appFromCoreAnalyticsProvider: Provider<CoreAnalytics, App>,
    appFromListActivityProvider: Provider<ListActivity, App>
) :
    ListActivityComponentDependencies by ListActivityComponentResolver(
        definition,
        appFromListActivityProvider
    ),
    CoreAnalyticsComponentDependencies by CoreAnalyticsComponentResolver(
        definition,
        appFromCoreAnalyticsProvider
    ) {

    private class ListActivityComponentResolver(
        private val definition: AppComponentDefinition,
        private val appFromListActivityProvider: Provider<ListActivity, App>
    ) : ListActivityComponentDependencies {

        override val analyticsProvider: Provider<ListActivity, Analytics>
            get() = definition
                .analyticsProvider
                .mapOwner(appFromListActivityProvider)
    }

    private class CoreAnalyticsComponentResolver(
        private val definition: AppComponentDefinition,
        private val appFromCoreAnalyticsProvider: Provider<CoreAnalytics, App>
    ) : CoreAnalyticsComponentDependencies {

        override fun getContext(coreAnalytics: CoreAnalytics): Context {
            return definition
                .contextProvider
                .mapOwner(appFromCoreAnalyticsProvider)
                .invoke(coreAnalytics)
        }
    }

    companion object {
        fun App.appComponent(
            definition: AppComponentDefinition,
            appFromCoreAnalyticsProvider: Provider<CoreAnalytics, App>,
            appFromListActivityProvider: Provider<ListActivity, App>
        ) = AppComponent(
            definition = definition,
            appFromCoreAnalyticsProvider = appFromCoreAnalyticsProvider,
            appFromListActivityProvider = appFromListActivityProvider
        )
    }
}

class AppComponentDefinition private constructor(
    val analyticsProvider: Provider<App, Analytics>,
    val contextProvider: Provider<App, Context>
) {
    companion object {
        fun App.appComponentDefinition(
            coreAnalyticsComponentDefinition: CoreAnalyticsComponentDefinition,
            contextProvider: Provider<App, Context>
        ) = AppComponentDefinition(
            analyticsProvider = coreAnalyticsComponentDefinition.analyticsProvider.mapOwner(just { appComponentInstance }),
            contextProvider = contextProvider
        )
    }
}