package dm.uporov.core.favorites.impl

import android.content.Context
import dm.uporov.core.favorites.api.FavoritesInteractor
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

private const val PREFERENCES_NAME = "favorites"
private const val FAVORITES_KEY = "favorites"

class FavoritesInteractorImpl(context: Context) : FavoritesInteractor {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val favorites by lazy { preferences.getStringSet(FAVORITES_KEY, null) ?: mutableSetOf() }

    private val subject = BehaviorSubject.create<Set<String>>()

    override fun favoritesIdsObservable(): Observable<Set<String>> = subject.startWith(favorites)

    override  fun addToFavorites(id: String) {
        favorites.add(id)
        saveFavoritesIds(favorites)
        subject.onNext(favorites)
    }

    override  fun removeFromFavorites(id: String) {
        favorites.remove(id)
        saveFavoritesIds(favorites)
        subject.onNext(favorites)
    }

    private fun saveFavoritesIds(favorites: Set<String>) {
        preferences.edit()
            .putStringSet(FAVORITES_KEY, favorites)
            .apply()
    }
}