package dm.uporov.analytics

import android.content.Context
import dm.uporov.analytics.CoreAnalytics_ComponentDefinition.Companion.coreAnalytics_ComponentDefinition
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.provider.single

@MacheteFeature(
    dependencies = [Context::class]
)
object CoreAnalytics {

    val coreAnalyticsFeatureDefinition = coreAnalytics_ComponentDefinition(
        single {
//            AnalyticsImpl(it.getContext(this))
            object: Analytics {
                override fun sendEvent(event: String) {
                    //
                }
            }
        }
    )

}