package dm.uporov.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.analytics.Analytics
import dm.uporov.list.generated.injectListPresenter
import dm.uporov.list.generated.warmUpListActivityComponent
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    featureName = LIST_FEATURE,
    dependencies = [Analytics::class]
)
class ListActivity : AppCompatActivity(), ListView {

    private val listPresenter: ListPresenter by injectListPresenter()

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