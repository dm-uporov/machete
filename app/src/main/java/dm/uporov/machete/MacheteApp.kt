package dm.uporov.machete

import android.app.Application
import dm.uporov.analytics.Analytics
import dm.uporov.list.ListPresenterImpl
import dm.uporov.list.di.ListActivityComponent.Companion.listActivityComponent
import dm.uporov.list.di.ListActivityComponentDefinition.Companion.listActivityComponentDefinition
import dm.uporov.list.di.getAnalytics
import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.di.Machete.startMachete
import dm.uporov.machete.di.MacheteAppComponent.Companion.macheteAppComponent
import dm.uporov.machete.di.MacheteAppComponentDefinition.Companion.macheteAppComponentDefinition
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.single

@MacheteApplication(
    dependencies = [
        Analytics::class
    ]
)
class MacheteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initDi()
    }

    private fun initDi() {
        val macheteAppComponent = macheteAppComponent(
            definition = macheteAppComponentDefinition(
                single { object : Analytics {} }
            ),
            macheteAppFromListActivityProvider = just { this }
        )

        startMachete(
            macheteAppComponent,
            listActivityComponent(
                definition = listActivityComponentDefinition(
                    single { ListPresenterImpl(it, it.getAnalytics()) }
                ),
                dependencies = macheteAppComponent
            )
        )
    }
}