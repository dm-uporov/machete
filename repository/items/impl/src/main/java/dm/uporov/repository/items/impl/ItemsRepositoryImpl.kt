package dm.uporov.repository.items.impl

import android.content.Context
import dm.uporov.core.favorites.api.FavoritesInteractor
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class ItemsRepositoryImpl(
    private val context: Context,
    private val favoritesInteractor: FavoritesInteractor
) : ItemsRepository {

    override fun itemsFlow(): Flow<List<Item>> {
        return flowOf(
            listOf(
                Item("1", "Milk", "Milk description", 2.15),
                Item("2", "Meat", "Meat description", 10.95),
                Item("3", "Wine", "Wine description", 15.5)
            )
        )
    }

    override suspend fun addToFavorites(item: Item) =
        favoritesInteractor.addToFavorites(item.id)

    override suspend fun removeFromFavorites(item: Item) =
        favoritesInteractor.removeFromFavorites(item.id)
}