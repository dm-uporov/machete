package dm.uporov.app.generated

import dm.uporov.analytics.CoreAnalytics_ModuleDefinition
import dm.uporov.analytics.setCoreAnalytics_ModuleDefinition
import dm.uporov.app.generated.AppComponent.Companion.appComponent
import dm.uporov.list.ListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_ComponentDependencies

object Machete {

    fun startMachete(
        appComponentDefinition: AppComponentDefinition,
        listActivityComponentDefinition: ListActivity_ComponentDefinition,
        coreAnalyticsModuleDefinition: CoreAnalytics_ModuleDefinition
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
        setCoreAnalytics_ModuleDefinition(coreAnalyticsModuleDefinition)
    }
}