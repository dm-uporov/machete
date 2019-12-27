package dm.uporov.item_details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.core_analytics_api.Analytics
import dm.uporov.core_analytics_api.Event
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    required = [Analytics::class]
)
class ItemDetailsActivity : AppCompatActivity() {

    private val analytics by injectAnalytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics.sendEvent(Event("Item details are showed"))
    }
}