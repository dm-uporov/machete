package dm.uporov.list

import com.example.core_analytics_api.Analytics
import com.example.core_analytics_api.Event
import dm.uporov.repository_items_api.ItemsRepository

class ListPresenterImpl(
    private val view: ListView,
    private val analytics: Analytics,
    private val itemsRepository: ItemsRepository
) : ListPresenter {

    override fun start() {
        analytics.sendEvent(Event("ListPresenterImpl is started"))
        val items = itemsRepository.getItems()
        view.showItems(items)
    }
}