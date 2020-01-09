package dm.uporov.core.favorites

import android.content.Context
import dm.uporov.core_favorites.CoreFavoritesModuleDefinition
import dm.uporov.core.favorites.api.FavoritesInteractor
import dm.uporov.core.favorites.impl.FavoritesInteractorImpl
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    api = [FavoritesInteractor::class],
    required = [Context::class]
)
object CoreFavorites

val coreFavoritesModuleDefinition = CoreFavoritesModuleDefinition.coreFavoritesModuleDefinition(
    single { FavoritesInteractorImpl(it.getContext()) }
)