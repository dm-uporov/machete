package dm.uporov.repository.favorites_items.impl

import dm.uporov.core.favorites.api.FavoritesInteractor
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import dm.uporov.repository.favorites_items.api.FavoritesItemsRepository

class FavoritesItemsRepositoryImpl(
    private val allItemsRepository: ItemsRepository,
    private val favoritesInteractor: FavoritesInteractor
) : FavoritesItemsRepository {

    override fun getItems(): List<Item> {
        val favorites = favoritesInteractor.getFavoritesIds()
        return allItemsRepository.getItems().filter {
            favorites.contains(it.id)
        }
    }
}