package dm.uporov.feature_home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.core_analytics_api.Analytics
import dm.uporov.feature_home.generated.inflateListFragment
import dm.uporov.list.ListFragment
import dm.uporov.machete.annotation.MacheteFeature

@MacheteFeature(
    dependencies = [Analytics::class],
    features = [ListFragment::class]
)
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = ListFragment::class.java.newInstance()

        inflateListFragment(fragment)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit()
    }
}