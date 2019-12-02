package dm.uporov.app.generated

import dm.uporov.analytics.CoreAnalytics_ModuleDefinition
import dm.uporov.analytics.setCoreAnalytics_ModuleDefinition
import dm.uporov.app.generated.AppComponent.Companion.appComponent
import dm.uporov.list.ListActivity_Component.Companion.listActivity_Component
import dm.uporov.list.ListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_Component

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
        val listActivityComponent = listActivity_Component(
            listActivityComponentDefinition,
            ListActivityComponentResolver(appComponent)
        )
        setListActivity_Component(listActivityComponent)

        setCoreAnalytics_ModuleDefinition(coreAnalyticsModuleDefinition)
    }
}