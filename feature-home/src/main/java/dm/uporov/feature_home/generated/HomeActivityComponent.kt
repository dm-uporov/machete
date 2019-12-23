package dm.uporov.feature_home.generated

import com.example.core_analytics_api.Analytics
import dm.uporov.feature_home.HomeActivity
import dm.uporov.feature_home.HomeActivityComponentDefinition
import dm.uporov.feature_home.HomeActivityComponentDependencies
import dm.uporov.list.ListFragment
import dm.uporov.list.ListFragmentComponentDependencies
import dm.uporov.list.listFragmentComponentDefinition
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

private lateinit var instance: HomeActivityComponent

fun setHomeActivityComponent(component: HomeActivityComponent) {
  instance = component
}

private fun getComponent(): HomeActivityComponent {
  if (!::instance.isInitialized) throw MacheteIsNotInitializedException()
   return instance
}

fun HomeActivity.getAnalytics(): Analytics {
   return getComponent().analyticsProvider.invoke(this)
}

fun HomeActivity.injectAnalytics(): Lazy<Analytics> {
   return lazy { getComponent().analyticsProvider.invoke(this) }
}

fun HomeActivity.inflateListFragment(fragment: ListFragment) {
    dm.uporov.list.generated.setListFragmentComponent(
        fragment,
        dm.uporov.list.generated.ListFragmentComponent.listFragmentComponent(
            listFragmentComponentDefinition,
            ListFragmentComponentDependenciesResolver(getComponent())
        )
    )
}

class HomeActivityComponent private constructor(
  val analyticsProvider: Provider<HomeActivity, Analytics>,
  val homeActivityFromListFragmentProvider: Provider<ListFragment, HomeActivity>
) {
  companion object {
    fun homeActivityComponent(definition: HomeActivityComponentDefinition,
                              dependencies: HomeActivityComponentDependencies
    ): HomeActivityComponent {
                                  val homeActivityComponent = HomeActivityComponent(
                                      
          homeActivityFromListFragmentProvider = definition.homeActivityFromListFragmentProvider, 
          analyticsProvider = dependencies.analyticsProvider
                                      )
      dm.uporov.list.setListFragmentComponent(
              dm.uporov.list.ListFragmentComponent.listFragmentComponent(
                  definition.listFragmentComponentDefinition,
                  ListFragmentComponentDependenciesResolver(
                      homeActivityComponent
                  )
              )
          )
      return homeActivityComponent
    }
  }

}

class ListFragmentComponentDependenciesResolver(
    private val homeActivityComponent: HomeActivityComponent
) : ListFragmentComponentDependencies {
    override val analyticsProvider: Provider<ListFragment, Analytics>
        get() = homeActivityComponent
            .analyticsProvider
            .mapOwner(
                homeActivityComponent.homeActivityFromListFragmentProvider
            )
}
