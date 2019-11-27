package dm.uporov.machete.generated

import dm.uporov.list.generated.ListActivityComponent.Companion.listActivityComponent
import dm.uporov.list.generated.ListActivityComponentDefinition
import dm.uporov.list.generated.setListActivityComponentInstance
import dm.uporov.machete.App

object Machete {

    fun startMachete(
        appComponent: AppComponent,
        listActivityComponentDefinition: ListActivityComponentDefinition
    ) {
        setAppComponentInstance(appComponent)
        setListActivityComponentInstance(
            listActivityComponent(
                definition = listActivityComponentDefinition,
                dependencies = appComponent
            )
        )
    }
}