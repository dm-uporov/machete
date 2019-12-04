package dm.uporov.app.generated

import dm.uporov.analytics.CoreAnalytics_ModuleDefinition
import dm.uporov.analytics.setCoreAnalytics_ModuleDefinition
import dm.uporov.app.App_Component
import dm.uporov.app.App_Component.Companion.app_Component
import dm.uporov.app.App_ComponentDefinition
import dm.uporov.app.App_ComponentDependencies
import dm.uporov.app.setApp_Component
import dm.uporov.list.ListActivity_Component.Companion.listActivity_Component
import dm.uporov.list.ListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_Component

object Machete {

    fun startMachete(
        appComponentDefinition: App_ComponentDefinition,
        listActivityComponentDefinition: ListActivity_ComponentDefinition,
        coreAnalyticsModuleDefinition: CoreAnalytics_ModuleDefinition
    ) {
        val appComponent = app_Component(
            appComponentDefinition,
            object : App_ComponentDependencies {},
            coreAnalyticsModuleDefinition
        )

        setApp_Component(appComponent)

        val listActivityComponent = listActivity_Component(
            listActivityComponentDefinition,
            App_Component.ListActivity_ComponentDependencies_Resolver(appComponent)
        )
        setListActivity_Component(listActivityComponent)

        setCoreAnalytics_ModuleDefinition(coreAnalyticsModuleDefinition)
    }
}