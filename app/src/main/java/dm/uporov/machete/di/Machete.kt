package dm.uporov.machete.di

import dm.uporov.list.di.ListActivityComponent
import dm.uporov.list.di.setListActivityComponentInstance

object Machete {

    fun startMachete(
        macheteAppComponent: MacheteAppComponent,
        listActivityComponent: ListActivityComponent
    ) {
        setListActivityComponentInstance(listActivityComponent)
    }
}