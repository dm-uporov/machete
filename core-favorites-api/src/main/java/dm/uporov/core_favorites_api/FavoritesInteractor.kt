package dm.uporov.core_favorites_api

interface FavoritesInteractor {

    fun getFavoritesIds(): Set<String>

    fun addToFavorites(id: String)

    fun removeFromFavorites(id: String)
}