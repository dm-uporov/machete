package dm.uporov.repository_items_favorites

import android.content.Context
import dm.uporov.core.favorites.CoreFavorites
import dm.uporov.core.favorites.coreFavoritesModuleDefinition
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single
import dm.uporov.repository_items.ItemsRepositoryCore
import dm.uporov.repository_items.itemsRepositoryModule
import dm.uporov.repository_items_favorites.FavoritesItemsRepositoryCoreModuleDefinition.Companion.favoritesItemsRepositoryCoreModuleDefinition
import dm.uporov.repository_items_favorites_api.FavoritesItemsRepository
import dm.uporov.repository_items_favorites_impl.FavoritesItemsRepositoryImpl

@MacheteModule(
    api = [FavoritesItemsRepository::class],
    required = [Context::class],
    modules = [CoreFavorites::class, ItemsRepositoryCore::class]
)
object FavoritesItemsRepositoryCore

val favoritesItemsRepositoryModule = favoritesItemsRepositoryCoreModuleDefinition(
    single { FavoritesItemsRepositoryImpl(it.getItemsRepository(), it.getFavoritesInteractor()) },
    coreFavoritesModuleDefinition = coreFavoritesModuleDefinition,
    itemsRepositoryCoreModuleDefinition = itemsRepositoryModule
)