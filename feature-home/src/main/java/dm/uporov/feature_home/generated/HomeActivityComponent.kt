package dm.uporov.feature_home.generated

import com.example.core_analytics_api.Analytics
import dm.uporov.feature_home.HomeActivity
import dm.uporov.feature_home.HomeActivityComponentDefinition
import dm.uporov.feature_home.HomeActivityComponentDependencies
import dm.uporov.list.ListFragment
import dm.uporov.list.ListFragmentComponentDefinition
import dm.uporov.list.ListFragmentComponentDependencies
import dm.uporov.machete.exception.SubFeatureIsNotInitializedException
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

private val componentsList = mutableListOf<HomeActivityComponent>()

fun setHomeActivityComponent(component: HomeActivityComponent) {
    componentsList.add(component)
}

private fun HomeActivity.getComponent(): HomeActivityComponent {
    return componentsList.find { it.isRelevantFor(this) }
        ?: throw SubFeatureIsNotInitializedException(this::class)
}

fun HomeActivity.getAnalytics(): Analytics {
    return getComponent().analyticsProvider.invoke(this)
}

fun HomeActivity.injectAnalytics(): Lazy<Analytics> {
    return lazy { getComponent().analyticsProvider.invoke(this) }
}

fun HomeActivity.inflateListFragment(fragment: ListFragment) {
    val component = getComponent()
    dm.uporov.list.generated.setListFragmentComponent(
        fragment,
        dm.uporov.list.generated.ListFragmentComponent.listFragmentComponent(
            component.listFragmentComponentDefinition,
            ListFragmentComponentDependenciesResolver(component)
        )
    )
}

class HomeActivityComponent private constructor(
    val isRelevantFor: (HomeActivity) -> Boolean,
    val analyticsProvider: Provider<HomeActivity, Analytics>,
    val listFragmentParentProvider: Provider<ListFragment, HomeActivity>,
    val listFragmentComponentDefinition: ListFragmentComponentDefinition
) {
    companion object {
        fun homeActivityComponent(
            isRelevantFor: (HomeActivity) -> Boolean,
            definition: HomeActivityComponentDefinition,
            dependencies: HomeActivityComponentDependencies
        ): HomeActivityComponent {
            val homeActivityComponent = HomeActivityComponent(
                isRelevantFor = isRelevantFor,
                listFragmentParentProvider = definition.listFragmentParentProvider,
                analyticsProvider = dependencies.analyticsProvider,
                listFragmentComponentDefinition = definition.listFragmentComponentDefinition
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
            .mapOwner(homeActivityComponent.listFragmentParentProvider)
}
