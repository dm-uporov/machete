package dm.uporov.repository.items

import android.content.Context
import dm.uporov.core.favorites.CoreFavorites
import dm.uporov.core.favorites.coreFavoritesModuleDefinition
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single
import dm.uporov.repository.items.ItemsRepositoryCoreModuleDefinition.Companion.itemsRepositoryCoreModuleDefinition
import dm.uporov.repository.items.api.ItemsRepository
import dm.uporov.repository.items.impl.ItemsRepositoryImpl

@MacheteModule(
    api = [ItemsRepository::class],
    required = [Context::class],
    modules = [CoreFavorites::class]
)
object ItemsRepositoryCore

val itemsRepositoryModule = itemsRepositoryCoreModuleDefinition(
    single { ItemsRepositoryImpl(it.getContext(), it.getFavoritesInteractor()) },
    coreFavoritesModuleDefinition
)