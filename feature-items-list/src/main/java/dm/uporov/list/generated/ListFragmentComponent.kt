package dm.uporov.list.generated

import android.content.Context
import com.example.core_analytics_api.Analytics
import dm.uporov.list.*
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.mapOwner
import dm.uporov.repository_items.ItemsRepositoryCoreModuleDependencies
import dm.uporov.repository_items_api.ItemsRepository


fun ListFragment.getAnalytics(): Analytics {
    return component.analyticsProvider.invoke(this)
}

fun ListFragment.getItemsAdapter(): ItemsAdapter {
    return component.itemsAdapterProvider.invoke(this)
}

fun ListFragment.getListPresenter(): ListPresenter {
    return component.listPresenterProvider.invoke(this)
}

fun ListFragment.getItemsRepository(): ItemsRepository {
    return component.itemsRepositoryProvider.invoke(this)
}

fun ListFragment.getContext(): Context {
    return component.contextProvider.invoke(this)
}

fun ListFragment.injectAnalytics(): Lazy<Analytics> {
    return lazy { component.analyticsProvider.invoke(this) }
}

fun ListFragment.injectItemsAdapter(): Lazy<ItemsAdapter> {
    return lazy { component.itemsAdapterProvider.invoke(this) }
}

fun ListFragment.injectListPresenter(): Lazy<ListPresenter> {
    return lazy { component.listPresenterProvider.invoke(this) }
}

fun ListFragment.injectItemsRepository(): Lazy<ItemsRepository> {
    return lazy { component.itemsRepositoryProvider.invoke(this) }
}

fun ListFragment.injectContext(): Lazy<Context> {
    return lazy { component.contextProvider.invoke(this) }
}

class ListFragmentComponent private constructor(
    val analyticsProvider: Provider<ListFragment, Analytics>,
    val itemsAdapterProvider: Provider<ListFragment, ItemsAdapter>,
    val listPresenterProvider: Provider<ListFragment, ListPresenter>,
    val itemsRepositoryProvider: Provider<ListFragment, ItemsRepository>,
    val contextProvider: Provider<ListFragment, Context>
) {
    companion object {
        fun listFragmentComponent(
            definition: ListFragmentComponentDefinition, dependencies:
            ListFragmentComponentDependencies
        ): ListFragmentComponent {
            return ListFragmentComponent(

                itemsAdapterProvider = definition.itemsAdapterProvider,
                listPresenterProvider = definition.listPresenterProvider,
                contextProvider = definition.contextProvider,
                analyticsProvider = dependencies.analyticsProvider,
                itemsRepositoryProvider =
                definition.itemsRepositoryCoreModuleDefinition.itemsRepositoryProvider.mapOwner(just
                { ItemsRepositoryCoreModuleDependenciesResolver(definition, it) })
            )
        }
    }

    private class ItemsRepositoryCoreModuleDependenciesResolver(
        private val definition:
        ListFragmentComponentDefinition, private val listFragment: ListFragment
    ) :
        ItemsRepositoryCoreModuleDependencies {
        override fun getContext(): Context {
            return definition.contextProvider.invoke(listFragment)
        }

        override fun getItemsRepository(): ItemsRepository = definition
            .itemsRepositoryCoreModuleDefinition
            .itemsRepositoryProvider
            .invoke(this)
    }
}
