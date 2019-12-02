package dm.uporov.analytics

import android.content.Context
import android.util.Log
import dm.uporov.machete.annotation.ModuleScope

@ModuleScope(module = CoreAnalytics::class)
interface Analytics {

    fun sendEvent(event: String)
}

class AnalyticsImpl(context: Context) : Analytics {

    override fun sendEvent(event: String) {
        Log.d(AnalyticsImpl::class.java.simpleName, event)
    }
}