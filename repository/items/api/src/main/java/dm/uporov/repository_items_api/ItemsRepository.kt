package dm.uporov.repository_items_api

interface ItemsRepository {

    fun getItems(): List<Item>
}