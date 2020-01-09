package dm.uporov.feature_home

import dm.uporov.list.listFragmentComponentDefinition
import dm.uporov.machete.provider.parentProvider
import dm.uporov.repository_items.itemsRepositoryModule

val homeActivityComponentDefinition =
    HomeActivityComponentDefinition.homeActivityComponentDefinition(
        listFragmentParentProvider = parentProvider({ it.activity is HomeActivity }, { it.activity as HomeActivity }),
        listFragmentComponentDefinition = listFragmentComponentDefinition,
        itemsRepositoryCoreModuleDefinition = itemsRepositoryModule
    )