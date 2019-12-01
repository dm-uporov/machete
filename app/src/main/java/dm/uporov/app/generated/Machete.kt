package dm.uporov.app.generated

import dm.uporov.list.ListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_ComponentDefinition
import dm.uporov.list.setListActivity_ComponentDependencies

object Machete {

    fun startMachete(
        appComponent: AppComponent,
        listActivityComponent: ListActivity_ComponentDefinition
    ) {
        setAppComponentInstance(appComponent)
        setListActivity_ComponentDefinition(listActivityComponent)
        setListActivity_ComponentDependencies(
            ListActivityComponentResolver(appComponent)
        )
    }
}