package dm.uporov.app.generated

import dm.uporov.analytics.generated.CoreAnalyticsModuleDefinition
import dm.uporov.analytics.generated.setCoreAnalyticsModuleDefinition
import dm.uporov.app.generated.AppComponent.Companion.appComponent
import dm.uporov.list.ListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_ComponentDependencies

object Machete {

    fun startMachete(
        appComponentDefinition: AppComponentDefinition,
        listActivityComponentDefinition: ListActivity_ComponentDefinition,
        coreAnalyticsModuleDefinition: CoreAnalyticsModuleDefinition
    ) {
        val appComponent = appComponent(
            appComponentDefinition,
            coreAnalyticsModuleDefinition
        )

        setAppComponentInstance(appComponent)
        setListActivity_ComponentDefinition(listActivityComponentDefinition)
        setListActivity_ComponentDependencies(
            ListActivityComponentResolver(appComponent)
        )
        setCoreAnalyticsModuleDefinition(coreAnalyticsModuleDefinition)
    }
}