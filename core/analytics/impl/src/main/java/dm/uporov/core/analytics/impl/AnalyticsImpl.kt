package dm.uporov.core.analytics.impl

import android.content.Context
import android.util.Log
import dm.uporov.core.analytics.api.Analytics
import dm.uporov.core.analytics.api.Event

private val TAG = AnalyticsImpl::class.java.simpleName

class AnalyticsImpl(context: Context) : Analytics {

    override fun sendEvent(event: Event) {
        Log.d(TAG, event.name)
    }
}