package dm.uporov.machete.di

import dm.uporov.list.di.ListActivityComponent
import dm.uporov.list.di.setListActivityComponentInstance

object Machete {

    fun startMachete(
        appComponent: AppComponent,
        listActivityComponent: ListActivityComponent
    ) {
        setAppComponentInstance(appComponent)
        setListActivityComponentInstance(listActivityComponent)
    }
}