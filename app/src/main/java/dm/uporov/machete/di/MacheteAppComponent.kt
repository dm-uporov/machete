package dm.uporov.machete.di

import dm.uporov.analytics.Analytics
import dm.uporov.list.ListActivity
import dm.uporov.list.di.ListActivityComponentDependencies
import dm.uporov.machete.MacheteApp
import dm.uporov.machete.provider.Provider
import dm.uporov.machete.provider.mapOwner

private lateinit var macheteAppComponentInstance: MacheteAppComponent

fun setMacheteAppComponentInstance(instance: MacheteAppComponent) {
    macheteAppComponentInstance = instance
}

private fun getMacheteAppComponent(): MacheteAppComponent {
    if (!::macheteAppComponentInstance.isInitialized) throw
    dm.uporov.machete.exception.DakkerIsNotInitializedException()

    return macheteAppComponentInstance
}

fun MacheteApp.getAnalytics(): Analytics {
    return getMacheteAppComponent()
        .definition
        .analyticsProvider(this)
}

fun MacheteApp.injectResources(): Lazy<Analytics> = lazy {
    getMacheteAppComponent()
        .definition
        .analyticsProvider(this)
}

class MacheteAppComponent private constructor(
    val definition: MacheteAppComponentDefinition,
    macheteAppFromListActivityProvider: Provider<ListActivity, MacheteApp>
) : ListActivityComponentDependencies by ListActivityComponentResolver(
    definition,
    macheteAppFromListActivityProvider
) {

    private class ListActivityComponentResolver(
        private val definition: MacheteAppComponentDefinition,
        private val macheteAppFromListActivityProvider: Provider<ListActivity, MacheteApp>
    ) : ListActivityComponentDependencies {

        override val analyticsProvider: Provider<ListActivity, Analytics>
            get() = definition.analyticsProvider.mapOwner(macheteAppFromListActivityProvider)
    }

    companion object {
        fun MacheteApp.macheteAppComponent(
            definition: MacheteAppComponentDefinition,
            macheteAppFromListActivityProvider: Provider<ListActivity, MacheteApp>
        ) = MacheteAppComponent(
            definition = definition,
            macheteAppFromListActivityProvider = macheteAppFromListActivityProvider
        )
    }
}

class MacheteAppComponentDefinition private constructor(
    val analyticsProvider: Provider<MacheteApp, Analytics>
) {
    companion object {
        fun MacheteApp.macheteAppComponentDefinition(
            analyticsProvider: Provider<MacheteApp, Analytics>
        ) = MacheteAppComponentDefinition(analyticsProvider)
    }
}