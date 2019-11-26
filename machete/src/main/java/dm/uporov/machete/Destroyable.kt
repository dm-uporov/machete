package dm.uporov.machete

interface Destroyable {

    fun addObserver(onDestroyObserver: OnDestroyObserver)

    fun removeObserver(onDestroyObserver: OnDestroyObserver)
}

interface OnDestroyObserver {

    fun onDestroy()
}