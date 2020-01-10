package dm.uporov.feature.items_list

import dm.uporov.core.analytics.api.Analytics
import dm.uporov.core.analytics.api.Event
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.repository.items.api.ItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FeatureScope(feature = ListFragment::class)
interface ListPresenter {

    fun start()
}

internal class ListPresenterImpl(
    private val view: ListView,
    private val analytics: Analytics,
    private val itemsRepository: ItemsRepository,
    private val coroutineScope: CoroutineScope
) : ListPresenter {

    @ExperimentalCoroutinesApi
    override fun start() {
        analytics.sendEvent(Event("ListPresenterImpl is started"))
        itemsRepository.itemsFlow()
            .onEach { view.showItems(it) }
            .launchIn(coroutineScope)
    }
}