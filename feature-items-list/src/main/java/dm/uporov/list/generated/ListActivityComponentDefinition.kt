package dm.uporov.list.generated

import dm.uporov.list.ListActivity
import dm.uporov.list.ListPresenter
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider

private lateinit var definition: ListActivityComponentDefinition

fun setListActivityComponentDefinition(instance: ListActivityComponentDefinition) {
    definition = instance
}

private fun getListActivityComponentDefinition(): ListActivityComponentDefinition {
    if (!::definition.isInitialized) throw
    MacheteIsNotInitializedException()

    return definition
}

fun ListActivity.getListPresenter(): ListPresenter {
    return getListActivityComponentDefinition().listActivityListPresenterProvider(this)
}

fun ListActivity.injectListPresenter(): Lazy<ListPresenter> = lazy {
    getListActivityComponentDefinition().listActivityListPresenterProvider(this)
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