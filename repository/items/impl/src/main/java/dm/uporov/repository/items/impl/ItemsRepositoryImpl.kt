package dm.uporov.repository.items.impl

import android.content.Context
import dm.uporov.core.favorites.api.FavoritesInteractor
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import io.reactivex.Observable

open class ItemsRepositoryImpl(
    private val context: Context,
    private val favoritesInteractor: FavoritesInteractor
) : ItemsRepository {

    override fun itemsObservable(): Observable<List<Item>> {
        return Observable.just(
            listOf(
                Item("1", "Milk", "Milk description", 2.15),
                Item("2", "Meat", "Meat description", 10.95),
                Item("3", "Wine", "Wine description", 15.5)
            )
        )
    }

    override fun addToFavorites(item: Item) =
        favoritesInteractor.addToFavorites(item.id)

    override fun removeFromFavorites(item: Item) =
        favoritesInteractor.removeFromFavorites(item.id)
}