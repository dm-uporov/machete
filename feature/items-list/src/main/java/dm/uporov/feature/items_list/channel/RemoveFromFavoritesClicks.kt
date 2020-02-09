package dm.uporov.feature.items_list.channel

import dm.uporov.repository.items.api.Item
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class RemoveFromFavoritesClicks {

    private val subject: Subject<Item> = BehaviorSubject.create()

    val observable: Observable<Item> = subject
    val observer: Observer<Item> = subject

}