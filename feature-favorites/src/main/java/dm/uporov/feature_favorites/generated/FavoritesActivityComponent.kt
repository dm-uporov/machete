package dm.uporov.feature_favorites.generated

import com.example.core_analytics_api.Analytics
import dm.uporov.feature_favorites.FavoritesActivity
import dm.uporov.feature_favorites.FavoritesActivityComponentDefinition
import dm.uporov.feature_favorites.FavoritesActivityComponentDependencies
import dm.uporov.list.ListFragment
import dm.uporov.list.ListFragmentComponentDependencies
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.factory
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.mapOwner
import dm.uporov.machete.provider.single
import kotlin.Lazy

private lateinit var instance: FavoritesActivityComponent

fun setFavoritesActivityComponent(component: FavoritesActivityComponent) {
  instance = component
}

private fun getComponent(): FavoritesActivityComponent {
  if (!::instance.isInitialized) throw MacheteIsNotInitializedException()
   return instance
}

fun FavoritesActivity.getAnalytics(): Analytics {
   return getComponent().analyticsProvider.invoke(this)
}

fun FavoritesActivity.injectAnalytics(): Lazy<Analytics> {
   return lazy { getComponent().analyticsProvider.invoke(this) }
}

class FavoritesActivityComponent private constructor(
  val analyticsProvider: Provider<FavoritesActivity, Analytics>,
  val listFragmentParentProvider: Provider<ListFragment, FavoritesActivity>
) {
  companion object {
    fun favoritesActivityComponent(definition: FavoritesActivityComponentDefinition,
                                   dependencies: FavoritesActivityComponentDependencies
    ): FavoritesActivityComponent {
                                  val favoritesActivityComponent = FavoritesActivityComponent(
                                      
          listFragmentParentProvider = definition.listFragmentParentProvider,
          analyticsProvider = dependencies.analyticsProvider
                                      )
      dm.uporov.list.setListFragmentComponent(
              dm.uporov.list.ListFragmentComponent.listFragmentComponent(
                  definition.listFragmentComponentDefinition,
                  ListFragmentComponentDependenciesResolver(
                      favoritesActivityComponent
                  )
              )
          )
      return favoritesActivityComponent
    }
  }
}

class ListFragmentComponentDependenciesResolver(
    private val favoritesActivityComponent: FavoritesActivityComponent
) : ListFragmentComponentDependencies {
    override val analyticsProvider: Provider<ListFragment, Analytics>
        get() = favoritesActivityComponent
            .analyticsProvider
            .mapOwner(
                favoritesActivityComponent.listFragmentParentProvider
            )
}
