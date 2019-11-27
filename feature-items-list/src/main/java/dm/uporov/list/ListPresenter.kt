package dm.uporov.list

import dm.uporov.analytics.Analytics

interface ListView {

    fun showItems(items: List<String>)
}

interface ListPresenter {

    fun start()
}

class ListPresenterImpl(
    private val view: ListView,
    private val analytics: Analytics
): ListPresenter {

    override fun start() {
        analytics.sendEvent("ListPresenterImpl is started")
        view.showItems(emptyList())
    }
}