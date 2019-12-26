package dm.uporov.core_favorites

import android.content.Context
import dm.uporov.core_favorites_api.FavoritesInteractor
import dm.uporov.core_favorites_impl.FavoritesInteractorImpl
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single

@MacheteModule(
    dependencies = [Context::class],
    provide = [FavoritesInteractor::class]
)
object CoreFavorites

val coreFavoritesModuleDefinition = CoreFavoritesModuleDefinition.coreFavoritesModuleDefinition(
    single { FavoritesInteractorImpl(it.getContext()) }
)