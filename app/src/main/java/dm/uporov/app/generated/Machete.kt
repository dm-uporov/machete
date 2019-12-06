package dm.uporov.app.generated

import dm.uporov.app.AppComponentDefinition
import dm.uporov.app.AppComponent.Companion.appComponent
import dm.uporov.app.AppComponentDependencies

object Machete {

    fun startMachete(appComponentDefinition: AppComponentDefinition) {
        dm.uporov.app.setAppComponent(appComponent(
            appComponentDefinition,
            object : AppComponentDependencies {}
        ))
    }
}