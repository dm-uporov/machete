package dm.uporov.machete.generated

import dm.uporov.list.generated.ListActivityComponentDefinition
import dm.uporov.list.generated.setListActivityComponentDefinition
import dm.uporov.list.generated.setListActivityComponentDependenciesResolver

object Machete {

    fun startMachete(
        appComponent: AppComponent,
        listActivityComponentDefinition: ListActivityComponentDefinition
    ) {
        setAppComponentInstance(appComponent)
        setListActivityComponentDefinition(listActivityComponentDefinition)
        setListActivityComponentDependenciesResolver(appComponent)
    }
}