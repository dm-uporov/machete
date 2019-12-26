package dm.uporov.list

import dm.uporov.core_analytics_api.Analytics
import dm.uporov.core_analytics_api.Event
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.repository_items_api.ItemsRepository

@FeatureScope(feature = ListFragment::class)
interface ListPresenter {

    fun start()
}

internal class ListPresenterImpl(
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