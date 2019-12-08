package dm.uporov.feature_favorites

import dm.uporov.list.listFragmentComponentDefinition
import dm.uporov.machete.provider.just

val favoritesActivityComponentDefinition = FavoritesActivityComponentDefinition.favoritesActivityComponentDefinition(
    favoritesActivityFromListFragmentProvider = just { it.activity as FavoritesActivity },
    listFragmentComponentDefinition = listFragmentComponentDefinition
)