package dm.uporov.machete.provider

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import dm.uporov.machete.Destroyable
import dm.uporov.machete.ModuleDependencies
import dm.uporov.machete.OnDestroyObserver
import java.util.concurrent.ConcurrentHashMap

// Provider for scope single dependency
fun <O : Any, T> single(provide: (O) -> T) = SingleProvider(provide)

class SingleProvider<in O: Any, out T> internal constructor(
    private val provider: (O) -> T
) : Provider<O, T> {

    private val ownersToValuesMap = ConcurrentHashMap<O, T>()

    override fun invoke(scopeOwner: O): T {
        synchronized(this) {
            with(ownersToValuesMap[scopeOwner]) {
                if (this == null) {
                    subscribeOnDestroyCallback(scopeOwner)
                    val newValue = provider(scopeOwner)
                    ownersToValuesMap[scopeOwner] = newValue
                    return newValue
                }
                return this
            }
        }
    }

    private fun subscribeOnDestroyCallback(scopeOwner: Any) {
        if (scopeOwner is ModuleDependencies) {
            val featureOwner = scopeOwner.featureOwner
            subscribeOnLifecycle(featureOwner, scopeOwner)
            subscribeOnDestroyable(featureOwner, scopeOwner)
        } else {
            subscribeOnLifecycle(scopeOwner)
            subscribeOnDestroyable(scopeOwner)
        }
    }

    private fun subscribeOnLifecycle(scopeOwner: Any, trashable: Any = scopeOwner) {
        if (scopeOwner is LifecycleOwner) {
            val lifecycle = scopeOwner.lifecycle
            lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    lifecycle.removeObserver(this)
                    trashable.trashValue()
                }
            })
        }
    }

    private fun subscribeOnDestroyable(scopeOwner: Any, trashable: Any = scopeOwner) {
        if (scopeOwner is Destroyable) {
            object : OnDestroyObserver {
                override fun onDestroy() {
                    scopeOwner.removeObserver(this)
                    trashable.trashValue()
                }
            }.run {
                scopeOwner.addObserver(this)
            }
        }
    }

    private fun Any.trashValue() {
        synchronized(this@SingleProvider) {
            ownersToValuesMap.remove(this)
        }
    }
}