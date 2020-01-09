package dm.uporov.core.favorites.impl

import android.content.Context
import dm.uporov.core.favorites.api.FavoritesInteractor

private const val FAVORITES_PREFERENCES = "favorites"
private const val FAVORITES_KEY = "favorites"

class FavoritesInteractorImpl(context: Context) : FavoritesInteractor {

    private val preferences =
        context.getSharedPreferences(FAVORITES_PREFERENCES, Context.MODE_PRIVATE)

    override fun getFavoritesIds(): Set<String> {
        return preferences.getStringSet(FAVORITES_KEY, null) ?: emptySet()
    }

    override fun addToFavorites(id: String) {
        setFavoritesIds(getFavoritesIds().plus(id))
    }

    override fun removeFromFavorites(id: String) {
        setFavoritesIds(getFavoritesIds().minus(id))
    }

    private fun setFavoritesIds(favorites: Set<String>) {
        preferences.edit()
            .putStringSet(FAVORITES_KEY, favorites)
            .apply()
    }
}