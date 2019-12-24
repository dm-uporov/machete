package dm.uporov.app.generated

import android.content.Context
import dm.uporov.analytics.CoreAnalyticsModuleDefinition
import dm.uporov.app.App
import dm.uporov.feature_favorites.FavoritesActivity
import dm.uporov.feature_favorites.FavoritesActivityComponentDefinition
import dm.uporov.feature_home.HomeActivity
import dm.uporov.feature_home.HomeActivityComponentDefinition
import dm.uporov.machete.provider.ParentProvider
import dm.uporov.machete.provider.Provider

class AppComponentDefinition(
  val contextProvider: Provider<App, Context>,
  val appFromHomeActivityProvider: ParentProvider<HomeActivity, App>,
  val appFromFavoritesActivityProvider: ParentProvider<FavoritesActivity, App>,
  val homeActivityComponentDefinition: HomeActivityComponentDefinition,
  val favoritesActivityComponentDefinition: FavoritesActivityComponentDefinition,
  val coreAnalyticsModuleDefinition: CoreAnalyticsModuleDefinition
) {
  companion object {
    fun appComponentDefinition(
      contextProvider: Provider<App, Context>,
      appFromHomeActivityProvider: ParentProvider<HomeActivity, App>,
      appFromFavoritesActivityProvider: ParentProvider<FavoritesActivity, App>,
      homeActivityComponentDefinition: HomeActivityComponentDefinition,
      favoritesActivityComponentDefinition: FavoritesActivityComponentDefinition,
      coreAnalyticsModuleDefinition: CoreAnalyticsModuleDefinition
    ): AppComponentDefinition = AppComponentDefinition(
    contextProvider = contextProvider, appFromHomeActivityProvider = appFromHomeActivityProvider,
        appFromFavoritesActivityProvider = appFromFavoritesActivityProvider,
        homeActivityComponentDefinition = homeActivityComponentDefinition,
        favoritesActivityComponentDefinition = favoritesActivityComponentDefinition,
        coreAnalyticsModuleDefinition = coreAnalyticsModuleDefinition
    )
  }
}
