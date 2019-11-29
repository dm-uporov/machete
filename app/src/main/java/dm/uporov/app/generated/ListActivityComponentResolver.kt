package dm.uporov.app.generated

import dm.uporov.analytics.Analytics
import dm.uporov.list.ListActivity
import dm.uporov.list.generated.ListActivityComponentDependencies
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

class ListActivityComponentResolver(
    private val definition: AppComponentDefinition
) : ListActivityComponentDependencies {

    override val analyticsProvider: Provider<ListActivity, Analytics>
        get() = definition
            .analyticsProvider
            .mapOwner(definition.appFromListActivityProvider)
}