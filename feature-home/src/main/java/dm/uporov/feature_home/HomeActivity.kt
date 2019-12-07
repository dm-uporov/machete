package dm.uporov.feature_home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.core_analytics_api.Analytics
import dm.uporov.list.ListFragment
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    dependencies = [Analytics::class],
    features = [ListFragment::class]
)
class HomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ListFragment::class.java.newInstance())
            .commit()
    }
}