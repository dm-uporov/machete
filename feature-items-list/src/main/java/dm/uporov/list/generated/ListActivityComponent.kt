package dm.uporov.list.generated

import dm.uporov.analytics.Analytics
import dm.uporov.list.ListActivity
import dm.uporov.list.ListPresenter
import dm.uporov.machete.provider.Provider

private lateinit var listActivityComponentInstance: ListActivityComponent

fun setListActivityComponentInstance(instance: ListActivityComponent) {
    listActivityComponentInstance = instance
}

fun getListActivityComponent(): ListActivityComponent {
    if (!::listActivityComponentInstance.isInitialized) throw
    dm.uporov.machete.exception.MacheteIsNotInitializedException()

    return listActivityComponentInstance
}

fun ListActivity.warmUpListActivityComponent() {
    getListPresenter()
    getAnalytics()
}

fun ListActivity.getListPresenter(): ListPresenter {
    return getListActivityComponent()
        .definition
        .listActivityListPresenterProvider(this)
}

fun ListActivity.injectListPresenter(): Lazy<ListPresenter> = lazy {
    getListActivityComponent()
        .definition
        .listActivityListPresenterProvider(this)
}

fun ListActivity.getAnalytics(): Analytics {
    return getListActivityComponent()
        .dependencies
        .analyticsProvider(this)
}

fun ListActivity.injectAnalytics(): Lazy<Analytics> = lazy {
    getListActivityComponent()
        .dependencies
        .analyticsProvider(this)
}

class ListActivityComponent private constructor(
    internal val definition: ListActivityComponentDefinition,
    internal val dependencies: ListActivityComponentDependencies
) {

    companion object {
        fun listActivityComponent(
            definition: ListActivityComponentDefinition,
            dependencies: ListActivityComponentDependencies
        ): ListActivityComponent {
            return ListActivityComponent(definition, dependencies)
        }
    }
}

class ListActivityComponentDefinition private constructor(
    val listActivityListPresenterProvider: Provider<ListActivity, ListPresenter>
) {
    companion object {
        fun listActivityComponentDefinition(
            listActivityListPresenterProvider: Provider<ListActivity, ListPresenter>
        ) = ListActivityComponentDefinition(
            listActivityListPresenterProvider = listActivityListPresenterProvider
        )
    }
}

interface ListActivityComponentDependencies {

    val analyticsProvider: Provider<ListActivity, Analytics>

}