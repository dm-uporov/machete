package dm.uporov.machete

import android.app.Application
import dm.uporov.analytics.Analytics
import dm.uporov.list.LIST_FEATURE
import dm.uporov.list.di.ListActivityComponent.Companion.listActivityComponent
import dm.uporov.list.listActivityComponentDefinition
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.di.AppComponent.Companion.appComponent
import dm.uporov.machete.di.AppComponentDefinition.Companion.macheteAppComponentDefinition
import dm.uporov.machete.di.Machete.startMachete
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.single

@MacheteApplication(
    features = [
        LIST_FEATURE
    ],
    dependencies = [
        Analytics::class
    ]
)
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initDi()
    }

    private fun initDi() {
        val macheteAppComponent = appComponent(
            definition = macheteAppComponentDefinition(
                single { object : Analytics {} }
            ),
            appFromListActivityProvider = just { this }
        )

        startMachete(
            macheteAppComponent,
            listActivityComponent(
                definition = listActivityComponentDefinition,
                dependencies = macheteAppComponent
            )
        )
    }
}