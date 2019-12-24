package dm.uporov.machete.provider

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import dm.uporov.machete.Destroyable
import dm.uporov.machete.OnDestroyObserver
import java.util.*

// Provider for scope single dependency
fun <O : Any, T> single(provide: (O) -> T) = SingleProvider(provide)

class SingleProvider<O, T> internal constructor(
    private val provider: (O) -> T
) : Provider<O, T> {

    private val weakHashMap = WeakHashMap<O, T>()
    private val ownersHashesToValuesMap = hashMapOf<Int, T>()

    override fun invoke(scopeOwner: O): T {
        //TODO check newInvoke function
        synchronized(this) {
            val ownerHash = scopeOwner.hashCode()
            with(ownersHashesToValuesMap[ownerHash]) {
                if (this == null) {
                    subscribeOnDestroyCallback(scopeOwner)
                    val newValue = provider(scopeOwner)
                    ownersHashesToValuesMap[ownerHash] = newValue
                    return newValue
                }
                return this
            }
        }
    }

    private fun newInvoke(scopeOwner: O): T {
        synchronized(this) {
            val dependency = weakHashMap[scopeOwner]
            if (dependency == null) {
                val newDependency = provider.invoke(scopeOwner)
                weakHashMap[scopeOwner] = newDependency
                return newDependency
            } else {
                return dependency
            }
        }
    }

    private fun subscribeOnDestroyCallback(scopeOwner: O) {
        when (scopeOwner) {
            is LifecycleOwner -> subscribeOnLifecycle(scopeOwner)
            is Destroyable -> subscribeOnDestroyable(scopeOwner)
        }
    }

    private fun subscribeOnLifecycle(scopeOwner: LifecycleOwner) {
        val lifecycle = scopeOwner.lifecycle
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                scopeOwner.trashValue()
            }
        })
    }

    private fun subscribeOnDestroyable(scopeOwner: Destroyable) {
        object : OnDestroyObserver {
            override fun onDestroy() {
                scopeOwner.removeObserver(this)
                scopeOwner.trashValue()
            }
        }.run {
            scopeOwner.addObserver(this)
        }
    }

    private fun Any.trashValue() {
        synchronized(this@SingleProvider) {
            ownersHashesToValuesMap.remove(hashCode())
        }
    }
}