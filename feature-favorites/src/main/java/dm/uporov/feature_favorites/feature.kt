package dm.uporov.feature_favorites

import dm.uporov.list.listFragmentComponentDefinition
import dm.uporov.machete.provider.parentProvider
import dm.uporov.repository_items_favorites.favoritesItemsRepositoryModule

val favoritesActivityComponentDefinition =
    FavoritesActivityComponentDefinition.favoritesActivityComponentDefinition(
        listFragmentParentProvider = parentProvider(
            { it.activity is FavoritesActivity },
            { it.activity as FavoritesActivity }),
        listFragmentComponentDefinition = listFragmentComponentDefinition,
        favoritesItemsRepositoryCoreModuleDefinition = favoritesItemsRepositoryModule
    )