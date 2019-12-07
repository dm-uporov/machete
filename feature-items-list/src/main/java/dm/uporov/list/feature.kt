package dm.uporov.list

import dm.uporov.list.ListFragmentComponentDefinition.Companion.listFragmentComponentDefinition
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.single
import dm.uporov.repository_items.itemsRepositoryModule

val listFragmentComponentDefinition = listFragmentComponentDefinition(
    single { ItemsAdapter() },
    single { ListPresenterImpl(it, it.getAnalytics(), it.getItemsRepository()) },
    just { it.activity!! },
    itemsRepositoryModule
)