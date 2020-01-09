package dm.uporov.core.favorites.api

interface FavoritesInteractor {

    fun getFavoritesIds(): Set<String>

    fun addToFavorites(id: String)

    fun removeFromFavorites(id: String)
}