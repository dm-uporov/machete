package dm.uporov.repository_items_favorites

import android.content.Context
import dm.uporov.core_favorites.CoreFavorites
import dm.uporov.core_favorites_api.FavoritesInteractor
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single
import dm.uporov.repository_items_api.ItemsRepository
import dm.uporov.repository_items_favorites.FavoritesItemsRepositoryCoreModuleDefinition.Companion.favoritesItemsRepositoryCoreModuleDefinition
import dm.uporov.repository_items_favorites_impl.FavoritesItemsRepositoryImpl
import dm.uporov.repository_items_impl.ItemsRepositoryImpl

@MacheteModule(
    modules = [CoreFavorites::class],
    provide = [ItemsRepository::class],
    dependencies = [Context::class, FavoritesInteractor::class]
)
object FavoritesItemsRepositoryCore

val favoritesItemsRepositoryModule = favoritesItemsRepositoryCoreModuleDefinition(
    single { FavoritesItemsRepositoryImpl(ItemsRepositoryImpl(it.getContext()), it.getFavoritesInteractor()) }
)