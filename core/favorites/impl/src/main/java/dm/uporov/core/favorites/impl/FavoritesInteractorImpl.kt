package dm.uporov.core.favorites.impl

import android.content.Context
import dm.uporov.core.favorites.api.FavoritesInteractor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart

private const val PREFERENCES_NAME = "favorites"
private const val FAVORITES_KEY = "favorites"

class FavoritesInteractorImpl(context: Context) : FavoritesInteractor {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val favorites by lazy { preferences.getStringSet(FAVORITES_KEY, null) ?: mutableSetOf() }

    private val channel: Channel<Set<String>> = Channel()

    @FlowPreview
    override fun favoritesIdsFlow() = channel.consumeAsFlow().onStart { emit(favorites) }

    override suspend fun addToFavorites(id: String) {
        favorites.add(id)
        saveFavoritesIds(favorites)
        channel.send(favorites)
    }

    override suspend fun removeFromFavorites(id: String) {
        favorites.remove(id)
        saveFavoritesIds(favorites)
        channel.send(favorites)
    }

    private fun saveFavoritesIds(favorites: Set<String>) {
        preferences.edit()
            .putStringSet(FAVORITES_KEY, favorites)
            .apply()
    }
}