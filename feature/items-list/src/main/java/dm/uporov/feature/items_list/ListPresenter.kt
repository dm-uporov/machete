package dm.uporov.feature.items_list

import dm.uporov.core.analytics.api.Analytics
import dm.uporov.core.analytics.api.Event
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@FeatureScope(feature = ListFragment::class)
interface ListPresenter {

    fun start()
}

internal class ListPresenterImpl(
    private val view: ListView,
    private val analytics: Analytics,
    private val itemsRepository: ItemsRepository,
    addToFavoritesClicksObservable: Observable<Item>,
    removeFromFavoritesClicksObservable: Observable<Item>
) : ListPresenter {

    init {
        addToFavoritesClicksObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe { itemsRepository.addToFavorites(it) }
        removeFromFavoritesClicksObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe { itemsRepository.removeFromFavorites(it) }
    }

    override fun start() {
        analytics.sendEvent(Event("ListPresenterImpl is started"))
        itemsRepository.itemsObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.showItems(it) }
    }
}