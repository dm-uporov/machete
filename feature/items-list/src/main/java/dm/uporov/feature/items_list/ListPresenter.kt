package dm.uporov.feature.items_list

import dm.uporov.core.analytics.api.Analytics
import dm.uporov.core.analytics.api.Event
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FeatureScope(feature = ListFragment::class)
interface ListPresenter {

    fun start()
}

@ExperimentalCoroutinesApi
internal class ListPresenterImpl(
    private val view: ListView,
    private val analytics: Analytics,
    private val itemsRepository: ItemsRepository,
    private val coroutineScope: CoroutineScope,
    addToFavoritesClicksFlow: Flow<Item>,
    removeFromFavoritesClicksFlow: Flow<Item>
) : ListPresenter {

    init {
        addToFavoritesClicksFlow
            .onEach { itemsRepository.addToFavorites(it) }
            .launchIn(coroutineScope)
        removeFromFavoritesClicksFlow
            .onEach { itemsRepository.removeFromFavorites(it) }
            .launchIn(coroutineScope)
    }

    @ExperimentalCoroutinesApi
    override fun start() {
        analytics.sendEvent(Event("ListPresenterImpl is started"))
        itemsRepository.itemsFlow()
            .onEach { view.showItems(it) }
            .launchIn(coroutineScope)
    }
}