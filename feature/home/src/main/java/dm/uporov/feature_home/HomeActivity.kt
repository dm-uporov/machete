package dm.uporov.feature_home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.core.analytics.api.Analytics
import dm.uporov.feature_favorites.FavoritesActivity
import dm.uporov.list.ListFragment
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.repository_items.ItemsRepositoryCore

@MacheteFeature(
    modules = [ItemsRepositoryCore::class],
    features = [ListFragment::class],
    required = [Analytics::class, Context::class]
)
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ListFragment::class.java.newInstance())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}