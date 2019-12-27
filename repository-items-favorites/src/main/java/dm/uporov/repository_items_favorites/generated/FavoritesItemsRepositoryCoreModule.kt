package dm.uporov.repository_items_favorites.generated

import android.content.Context
import dm.uporov.core_favorites.CoreFavoritesModuleDefinition
import dm.uporov.core_favorites.CoreFavoritesModuleDependencies
import dm.uporov.core_favorites_api.FavoritesInteractor
import dm.uporov.machete.ModuleDependencies
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider
import dm.uporov.repository_items_api.ItemsRepository

abstract class FavoritesItemsRepositoryCoreModuleDependencies(
    val definition: FavoritesItemsRepositoryCoreModuleDefinition
) : ModuleDependencies {

    abstract fun getContext(): Context

    abstract fun getItemsRepository(): ItemsRepository

    fun getFavoritesInteractor(): FavoritesInteractor {
        return CoreFavoritesModuleResolver(this, featureOwner).getFavoritesInteractor()
    }
}

class FavoritesItemsRepositoryCoreModuleDefinition private constructor(
    val itemsRepositoryProvider: Provider<FavoritesItemsRepositoryCoreModuleDependencies, ItemsRepository>,
    val coreFavoritesModuleDefinition: CoreFavoritesModuleDefinition
) {
    companion object {
        fun favoritesItemsRepositoryCoreModuleDefinition(
            itemsRepositoryProvider: Provider<FavoritesItemsRepositoryCoreModuleDependencies, ItemsRepository>,
            coreFavoritesModuleDefinition: CoreFavoritesModuleDefinition
        ): FavoritesItemsRepositoryCoreModuleDefinition =
            FavoritesItemsRepositoryCoreModuleDefinition(
                itemsRepositoryProvider = itemsRepositoryProvider,
                coreFavoritesModuleDefinition = coreFavoritesModuleDefinition
            )
    }
}

class CoreFavoritesModuleResolver(
    private val dependencies: FavoritesItemsRepositoryCoreModuleDependencies,
    override val featureOwner: Any
) : CoreFavoritesModuleDependencies {
    override fun getContext() = dependencies.getContext()

    override fun getFavoritesInteractor(): FavoritesInteractor {
        return dependencies
            .definition
            .coreFavoritesModuleDefinition
            .favoritesInteractorProvider
            .invoke(this)
    }

}