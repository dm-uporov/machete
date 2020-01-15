package dm.uporov.repository.items.api

import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

    fun itemsFlow(): Flow<List<Item>>

    suspend fun addToFavorites(item: Item)

    suspend fun removeFromFavorites(item: Item)
}