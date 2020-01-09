package dm.uporov.feature.items_list

import dm.uporov.feature.items_list.ListFragmentComponentDefinition.Companion.listFragmentComponentDefinition
import dm.uporov.machete.provider.single

val listFragmentComponent = listFragmentComponentDefinition(
    itemsAdapterProvider = single { ItemsAdapter() },
    listPresenterProvider = single { ListPresenterImpl(it, it.provideAnalytics(), it.provideItemsRepository()) }
)