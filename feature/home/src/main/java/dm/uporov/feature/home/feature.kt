package dm.uporov.feature.home

import dm.uporov.feature_home.HomeActivityComponentDefinition
import dm.uporov.feature.items_list.listFragmentComponentDefinition
import dm.uporov.machete.provider.parentProvider
import dm.uporov.repository.items.itemsRepositoryModule

val homeActivityComponentDefinition =
    HomeActivityComponentDefinition.homeActivityComponentDefinition(
        listFragmentParentProvider = parentProvider(
            { it.activity is HomeActivity },
            { it.activity as HomeActivity }),
        listFragmentComponentDefinition = listFragmentComponentDefinition,
        itemsRepositoryCoreModuleDefinition = itemsRepositoryModule
    )