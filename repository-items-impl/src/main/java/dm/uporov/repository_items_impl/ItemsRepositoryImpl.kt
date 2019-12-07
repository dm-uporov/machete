package dm.uporov.repository_items_impl

import android.content.Context
import dm.uporov.repository_items_api.Item
import dm.uporov.repository_items_api.ItemsRepository

class ItemsRepositoryImpl(
    val context: Context
) : ItemsRepository {

    override fun getItems(): List<Item> {
        return listOf(
            Item("1", "Milk", "Milk description", 2.15),
            Item("2", "Meat", "Meat description", 10.95),
            Item("3", "Wine", "Wine description", 15.5)
        )
    }
}