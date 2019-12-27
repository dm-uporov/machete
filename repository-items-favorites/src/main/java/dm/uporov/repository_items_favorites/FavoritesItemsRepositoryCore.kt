package dm.uporov.repository_items_favorites

import android.content.Context
import dm.uporov.core_favorites.CoreFavorites
import dm.uporov.core_favorites.coreFavoritesModuleDefinition
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single
import dm.uporov.repository_items_api.ItemsRepository
import dm.uporov.repository_items_favorites.generated.FavoritesItemsRepositoryCoreModuleDefinition.Companion.favoritesItemsRepositoryCoreModuleDefinition
import dm.uporov.repository_items_favorites_impl.FavoritesItemsRepositoryImpl
import dm.uporov.repository_items_impl.ItemsRepositoryImpl

@MacheteModule(
    api = [ItemsRepository::class],
    required = [Context::class],
    modules = [CoreFavorites::class]
)
object FavoritesItemsRepositoryCore

val favoritesItemsRepositoryModule = favoritesItemsRepositoryCoreModuleDefinition(
    single { FavoritesItemsRepositoryImpl(ItemsRepositoryImpl(it.getContext()), it.getFavoritesInteractor()) },
    coreFavoritesModuleDefinition = coreFavoritesModuleDefinition
)