package dm.uporov.core_analytics_impl

import android.content.Context
import android.util.Log
import dm.uporov.core_analytics_api.Analytics
import dm.uporov.core_analytics_api.Event

private val TAG = AnalyticsImpl::class.java.simpleName

class AnalyticsImpl(context: Context) : Analytics {

    override fun sendEvent(event: Event) {
        Log.d(TAG, event.name)
    }
}