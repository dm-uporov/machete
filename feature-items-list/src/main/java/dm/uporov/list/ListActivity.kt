package dm.uporov.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.analytics.Analytics
import dm.uporov.list.generated.injectAnalytics
import dm.uporov.list.generated.injectListPresenter
import dm.uporov.list.generated.warmUpListActivityComponent
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    dependencies = [Analytics::class]
)
class ListActivity : AppCompatActivity(), ListView {

    private val listPresenter by injectListPresenter()
    private val analytics by injectAnalytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // optional warming up
        warmUpListActivityComponent()

        listPresenter.start()
    }

    override fun showItems(items: List<String>) {
        analytics.sendEvent("Items are showed")
    }

}