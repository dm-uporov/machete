package dm.uporov.list

import dm.uporov.list.ListFragmentComponentDefinition.Companion.listFragmentComponentDefinition
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.single
import dm.uporov.repository_items.itemsRepositoryModule

val listFragmentComponentDefinition = listFragmentComponentDefinition(
    itemsAdapterProvider = single { ItemsAdapter() },
    listPresenterProvider = single { ListPresenterImpl(it, it.getAnalytics(), it.getItemsRepository()) },
    contextProvider = just { it.activity!! },
    itemsRepositoryCoreModuleDefinition = itemsRepositoryModule
)