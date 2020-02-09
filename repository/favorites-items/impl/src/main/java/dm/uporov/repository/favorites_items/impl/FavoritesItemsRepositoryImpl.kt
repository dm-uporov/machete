package dm.uporov.repository.favorites_items.impl

import dm.uporov.core.favorites.api.FavoritesInteractor
import dm.uporov.repository.favorites_items.api.FavoritesItemsRepository
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class FavoritesItemsRepositoryImpl(
    private val allItemsRepository: ItemsRepository,
    private val favoritesInteractor: FavoritesInteractor
) : FavoritesItemsRepository, ItemsRepository by allItemsRepository {

    override fun itemsObservable(): Observable<List<Item>> {
        return Observable.combineLatest(
            favoritesInteractor.favoritesIdsObservable(),
            allItemsRepository.itemsObservable(),
            BiFunction { favorites, items ->
                items.filter { favorites.contains(it.id) }
            }
        )
    }
}