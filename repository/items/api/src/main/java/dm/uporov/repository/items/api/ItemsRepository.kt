package dm.uporov.repository.items.api

import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

    fun itemsFlow(): Flow<List<Item>>
}