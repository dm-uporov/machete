package dm.uporov.list.generated

import dm.uporov.analytics.Analytics
import dm.uporov.list.ListActivity
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider

private lateinit var dependencies: ListActivityComponentDependencies

fun setListActivityComponentDependenciesResolver(dependenciesResolver: ListActivityComponentDependencies) {
    dependencies = dependenciesResolver
}

private fun getDependencies(): ListActivityComponentDependencies {
    if (!::dependencies.isInitialized) throw MacheteIsNotInitializedException()

    return dependencies
}

fun ListActivity.getAnalytics(): Analytics {
    return getDependencies()
        .analyticsProvider(this)
}

fun ListActivity.injectAnalytics(): Lazy<Analytics> = lazy {
    getDependencies()
        .analyticsProvider(this)
}

interface ListActivityComponentDependencies {

    val analyticsProvider: Provider<ListActivity, Analytics>

}