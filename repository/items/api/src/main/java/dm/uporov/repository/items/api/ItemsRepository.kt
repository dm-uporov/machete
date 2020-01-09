package dm.uporov.repository.items.api

interface ItemsRepository {

    fun getItems(): List<Item>
}