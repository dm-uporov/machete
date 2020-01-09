package dm.uporov.list

import dm.uporov.list.ListFragmentComponentDefinition.Companion.listFragmentComponentDefinition
import dm.uporov.machete.provider.single

val listFragmentComponentDefinition = listFragmentComponentDefinition(
    itemsAdapterProvider = single { ItemsAdapter() },
    listPresenterProvider = single { ListPresenterImpl(it, it.provideAnalytics(), it.provideItemsRepository()) }
)