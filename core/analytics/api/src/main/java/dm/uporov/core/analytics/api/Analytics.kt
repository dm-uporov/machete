package dm.uporov.core.analytics.api

interface Analytics {

    fun sendEvent(event: Event)
}
