package dm.uporov.feature_favorites

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.core_analytics_api.Analytics
import dm.uporov.list.ListFragment
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.repository_items_favorites.FavoritesItemsRepositoryCore

@MacheteFeature(
    modules = [FavoritesItemsRepositoryCore::class],
    dependencies = [Analytics::class, Context::class],
    features = [ListFragment::class]
)
class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ListFragment::class.java.newInstance())
            .commit()
    }
}