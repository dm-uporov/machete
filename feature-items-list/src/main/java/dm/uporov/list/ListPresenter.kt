package dm.uporov.list

import dm.uporov.machete.annotation.FeatureScope

@FeatureScope(feature = ListFragment::class)
interface ListPresenter {

    fun start()
}