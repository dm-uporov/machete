package dm.uporov.feature.items_list

import dm.uporov.list.ListFragmentComponentDefinition.Companion.listFragmentComponentDefinition
import dm.uporov.list.provideAnalytics
import dm.uporov.list.provideItemsRepository
import dm.uporov.machete.provider.single

val listFragmentComponentDefinition = listFragmentComponentDefinition(
    itemsAdapterProvider = single { ItemsAdapter() },
    listPresenterProvider = single { ListPresenterImpl(it, it.provideAnalytics(), it.provideItemsRepository()) }
)