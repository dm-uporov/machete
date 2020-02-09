package dm.uporov.core.favorites.api

import io.reactivex.Observable

interface FavoritesInteractor {

    fun favoritesIdsObservable(): Observable<Set<String>>

    fun addToFavorites(id: String)

    fun removeFromFavorites(id: String)
}