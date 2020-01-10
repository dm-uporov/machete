package dm.uporov.feature.favorites

import dm.uporov.feature.items_list.listFragmentComponent
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.parentProvider
import dm.uporov.repository.favorites_items.favoritesItemsRepositoryModule

val favoritesActivityComponentDefinition =
    FavoritesActivityComponentDefinition.favoritesActivityComponentDefinition(
        listFragmentParentProvider = parentProvider(
            { it.activity is FavoritesActivity },
            { it.activity as FavoritesActivity }
        ),
        itemsRepositoryProvider = just { it.provideFavoritesItemsRepository() },
        favoritesInteractorProvider = just {  },
        listFragmentComponentDefinition = listFragmentComponent,
        favoritesItemsRepositoryCoreModuleDefinition = favoritesItemsRepositoryModule
    )