package dm.uporov.core_analytics_api

interface Analytics {

    fun sendEvent(event: Event)
}
