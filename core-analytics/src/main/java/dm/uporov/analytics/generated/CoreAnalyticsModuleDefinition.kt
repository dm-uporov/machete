package dm.uporov.analytics.generated

import android.content.Context
import dm.uporov.analytics.Analytics
import dm.uporov.machete.exception.MacheteIsNotInitializedException
import dm.uporov.machete.provider.Provider


private lateinit var definition: CoreAnalyticsModuleDefinition

fun setCoreAnalyticsModuleDefinition(instance: CoreAnalyticsModuleDefinition) {
    definition = instance
}

private fun getDefinition(): CoreAnalyticsModuleDefinition {
    if (!::definition.isInitialized) throw MacheteIsNotInitializedException()
    return definition
}

fun CoreAnalyticsModuleDependencies.getAnalytics(): Analytics {
    return getDefinition().analyticsProvider.invoke(this)
}

class CoreAnalyticsModuleDefinition private constructor(
    val analyticsProvider: Provider<CoreAnalyticsModuleDependencies, Analytics>
) {
    companion object {
        fun coreAnalyticsComponentDefinition2(
            analyticsProvider: Provider<CoreAnalyticsModuleDependencies, Analytics>
        ): CoreAnalyticsModuleDefinition = CoreAnalyticsModuleDefinition(
            analyticsProvider = analyticsProvider
        )
    }
}

interface CoreAnalyticsModuleDependencies {

    fun getContext(): Context
}
