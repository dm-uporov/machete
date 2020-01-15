package dm.uporov.repository.favorites_items.impl

import dm.uporov.core.favorites.api.FavoritesInteractor
import dm.uporov.repository.favorites_items.api.FavoritesItemsRepository
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FavoritesItemsRepositoryImpl(
    private val allItemsRepository: ItemsRepository,
    private val favoritesInteractor: FavoritesInteractor
) : FavoritesItemsRepository, ItemsRepository by allItemsRepository {

    @ExperimentalCoroutinesApi
    override fun itemsFlow(): Flow<List<Item>> {
        return favoritesInteractor.favoritesIdsFlow()
            .combine(allItemsRepository.itemsFlow()) { favorites, items ->
                items.filter { favorites.contains(it.id) }
            }
    }
}