package dm.uporov.repository.favorites_items

import android.content.Context
import dm.uporov.core.favorites.CoreFavorites
import dm.uporov.core.favorites.coreFavoritesModuleDefinition
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single
import dm.uporov.repository.favorites_items.FavoritesItemsRepositoryCoreModuleDefinition.Companion.favoritesItemsRepositoryCoreModuleDefinition
import dm.uporov.repository.favorites_items.api.FavoritesItemsRepository
import dm.uporov.repository.favorites_items.impl.FavoritesItemsRepositoryImpl
import dm.uporov.repository.items.ItemsRepositoryCore
import dm.uporov.repository.items.itemsRepositoryModule

@MacheteModule(
    api = [FavoritesItemsRepository::class],
    required = [Context::class],
    modules = [CoreFavorites::class, ItemsRepositoryCore::class]
)
object FavoritesItemsRepositoryCore

val favoritesItemsRepositoryModule = favoritesItemsRepositoryCoreModuleDefinition(
    favoritesItemsRepositoryProvider = single {
        FavoritesItemsRepositoryImpl(
            it.getItemsRepository(),
            it.getFavoritesInteractor()
        )
    },
    coreFavoritesModuleDefinition = coreFavoritesModuleDefinition,
    itemsRepositoryCoreModuleDefinition = itemsRepositoryModule
)