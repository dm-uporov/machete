package dm.uporov.repository.items.api

import io.reactivex.Observable

interface ItemsRepository {

    fun itemsObservable(): Observable<List<Item>>

    fun addToFavorites(item: Item)

    fun removeFromFavorites(item: Item)
}