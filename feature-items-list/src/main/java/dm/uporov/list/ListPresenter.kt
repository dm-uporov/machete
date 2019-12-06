package dm.uporov.list

import com.example.core_analytics_api.Analytics
import com.example.core_analytics_api.Event
import dm.uporov.machete.annotation.FeatureScope

interface ListView {

    fun showItems(items: List<String>)
}

@FeatureScope(feature = ListActivity::class)
interface ListPresenter {

    fun start()
}

class ListPresenterImpl(
    private val view: ListView,
    private val analytics: Analytics
): ListPresenter {

    override fun start() {
        analytics.sendEvent(Event("ListPresenterImpl is started"))
        view.showItems(emptyList())
    }
}