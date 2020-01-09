package dm.uporov.repository_items_favorites_impl

import dm.uporov.core_favorites_api.FavoritesInteractor
import dm.uporov.repository_items_api.Item
import dm.uporov.repository_items_api.ItemsRepository
import dm.uporov.repository_items_favorites_api.FavoritesItemsRepository

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