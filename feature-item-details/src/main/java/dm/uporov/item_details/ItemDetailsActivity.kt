package dm.uporov.item_details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.analytics.Analytics
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    dependencies = [Analytics::class]
)
class ItemDetailsActivity : AppCompatActivity() {

    private val analytics by injectAnalytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics.sendEvent("Item details are showed")
    }
}