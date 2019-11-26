package dm.uporov.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.analytics.ANALYTICS_FEATURE
import dm.uporov.list.di.injectListPresenter
import dm.uporov.list.di.warmUpListActivityComponent
import dm.uporov.machete.annotation.Inject
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    featureName = LIST_FEATURE,
    includesFeatures = [ANALYTICS_FEATURE]
)
class ListActivity : AppCompatActivity(), ListView {

    @get:Inject
    val listPresenter: ListPresenter by injectListPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // optional warming up
        warmUpListActivityComponent()

        listPresenter.start()
    }

    override fun showItems(items: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}