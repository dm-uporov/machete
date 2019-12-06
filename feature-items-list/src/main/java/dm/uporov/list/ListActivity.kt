package dm.uporov.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.core_analytics_api.Analytics
import com.example.core_analytics_api.Event
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    dependencies = [Analytics::class]
)
class ListActivity : AppCompatActivity(), ListView {

    private val listPresenter by injectListPresenter()
    private val analytics by injectAnalytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listPresenter.start()
    }

    override fun showItems(items: List<String>) {
        analytics.sendEvent(Event("Items are showed"))
    }

}