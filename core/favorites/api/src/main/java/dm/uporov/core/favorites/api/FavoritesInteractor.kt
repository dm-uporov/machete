package dm.uporov.core.favorites.api

import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun favoritesIdsFlow(): Flow<Set<String>>

    suspend fun addToFavorites(id: String)

    suspend fun removeFromFavorites(id: String)
}