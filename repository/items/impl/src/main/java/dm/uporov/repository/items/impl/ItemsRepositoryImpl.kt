package dm.uporov.repository.items.impl

import android.content.Context
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ItemsRepositoryImpl(
    val context: Context
) : ItemsRepository {

    override fun itemsFlow(): Flow<List<Item>> {
        return flowOf(
            listOf(
                Item("1", "Milk", "Milk description", 2.15),
                Item("2", "Meat", "Meat description", 10.95),
                Item("3", "Wine", "Wine description", 15.5)
            )
        )
    }
}