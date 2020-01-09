package dm.uporov.feature.item_details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.core.analytics.api.Analytics
import dm.uporov.core.analytics.api.Event
import dm.uporov.item_details.injectAnalytics
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