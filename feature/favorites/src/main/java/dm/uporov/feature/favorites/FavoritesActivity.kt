package dm.uporov.feature.favorites

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dm.uporov.core.analytics.api.Analytics
import dm.uporov.feature.favorites.FavoritesActivityComponentDefinition.Companion.favoritesActivityComponentDefinition
import dm.uporov.feature.items_list.ListFragment
import dm.uporov.feature.items_list.listFragmentComponent
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.extensions.activityAsParentProvider
import dm.uporov.machete.provider.just
import dm.uporov.repository.favorites_items.FavoritesItemsRepositoryCore
import dm.uporov.repository.favorites_items.favoritesItemsRepositoryModule

val favoritesActivityComponent = favoritesActivityComponentDefinition(
    listFragmentParentProvider = activityAsParentProvider(),
    itemsRepositoryProvider = just { it.provideFavoritesItemsRepository() },
    listFragmentComponentDefinition = listFragmentComponent,
    favoritesItemsRepositoryCoreModuleDefinition = favoritesItemsRepositoryModule
)

@MacheteFeature(
    modules = [FavoritesItemsRepositoryCore::class],
    features = [ListFragment::class],
    required = [Analytics::class, Context::class]
)
class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ListFragment::class.java.newInstance())
            .commit()
    }
}