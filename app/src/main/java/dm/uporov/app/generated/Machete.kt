package dm.uporov.app.generated

import dm.uporov.list.generated.ListActivityComponentDefinition
import dm.uporov.list.generated.setListActivityComponentDefinition
import dm.uporov.list.generated.setListActivityComponentDependenciesResolver

object Machete {

    fun startMachete(
        appComponent: AppComponentDefinition,
        listActivityComponent: ListActivityComponentDefinition
    ) {
        setAppComponentInstance(appComponent)
        setListActivityComponentDefinition(listActivityComponent)
        setListActivityComponentDependenciesResolver(
            ListActivityComponentResolver(appComponent)
        )
    }
}