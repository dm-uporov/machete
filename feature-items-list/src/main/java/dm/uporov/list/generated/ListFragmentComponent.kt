package dm.uporov.list.generated

import android.content.Context
import com.example.core_analytics_api.Analytics
import dm.uporov.list.*
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.exception.SubFeatureIsNotInitializedException
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.mapOwner
import dm.uporov.repository_items.ItemsRepositoryCoreModuleDependencies
import dm.uporov.repository_items_api.ItemsRepository
import java.util.*
import kotlin.Lazy

private val componentsMap = WeakHashMap<ListFragment, ListFragmentComponent>()

fun setListFragmentComponent(owner: ListFragment, component: ListFragmentComponent) {
    componentsMap[owner] = component
}

private fun ListFragment.getComponent(): ListFragmentComponent {
    return componentsMap[this] ?: throw SubFeatureIsNotInitializedException(this::class)
}

fun ListFragment.getAnalytics(): Analytics {
    return getComponent().analyticsProvider.invoke(this)
}

fun ListFragment.getItemsAdapter(): ItemsAdapter {
    return getComponent().itemsAdapterProvider.invoke(this)
}

fun ListFragment.getListPresenter(): ListPresenter {
    return getComponent().listPresenterProvider.invoke(this)
}

fun ListFragment.getItemsRepository(): ItemsRepository {
    return getComponent().itemsRepositoryProvider.invoke(this)
}

fun ListFragment.getContext(): Context {
    return getComponent().contextProvider.invoke(this)
}

fun ListFragment.injectAnalytics(): Lazy<Analytics> {
    return lazy { getComponent().analyticsProvider.invoke(this) }
}

fun ListFragment.injectItemsAdapter(): Lazy<ItemsAdapter> {
    return lazy { getComponent().itemsAdapterProvider.invoke(this) }
}

fun ListFragment.injectListPresenter(): Lazy<ListPresenter> {
    return lazy { getComponent().listPresenterProvider.invoke(this) }
}

fun ListFragment.injectItemsRepository(): Lazy<ItemsRepository> {
    return lazy { getComponent().itemsRepositoryProvider.invoke(this) }
}

fun ListFragment.injectContext(): Lazy<Context> {
    return lazy { getComponent().contextProvider.invoke(this) }
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
            val listFragmentComponent = ListFragmentComponent(

                itemsAdapterProvider = definition.itemsAdapterProvider,
                listPresenterProvider = definition.listPresenterProvider,
                contextProvider = definition.contextProvider,
                analyticsProvider = dependencies.analyticsProvider,
                itemsRepositoryProvider =
                definition.itemsRepositoryCoreModuleDefinition.itemsRepositoryProvider.mapOwner(just
                { ItemsRepositoryCoreModuleDependenciesResolver(definition, it) })
            )
            return listFragmentComponent
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
