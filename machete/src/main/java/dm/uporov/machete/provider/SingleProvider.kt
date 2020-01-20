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

class SingleProvider<in O : Any, out T> internal constructor(
    private val provider: (O) -> T
) : Provider<O, T> {

    private val ownersToValuesMap = ConcurrentHashMap<O, T>()

    override fun invoke(scopeOwner: O): T {
        val dependency = ownersToValuesMap[scopeOwner]
        if (dependency != null) {
            return dependency
        }

        synchronized(scopeOwner) {
            val doubleCheckedDependency = ownersToValuesMap[scopeOwner]
            if (doubleCheckedDependency != null) {
                return doubleCheckedDependency
            }

            subscribeOnDestroyCallback(scopeOwner)
            return provider.invoke(scopeOwner)
                .also { ownersToValuesMap[scopeOwner] = it }
        }
    }

    private fun subscribeOnDestroyCallback(scopeOwner: Any, trashable: Any = scopeOwner) {
        if (scopeOwner is ModuleDependencies) {
            return subscribeOnDestroyCallback(scopeOwner.featureOwner, scopeOwner)
        }

        when (scopeOwner) {
            is LifecycleOwner -> subscribeOnLifecycle(scopeOwner, trashable)
            is Destroyable -> subscribeOnDestroyable(scopeOwner, trashable)
        }
    }

    private fun subscribeOnLifecycle(scopeOwner: LifecycleOwner, trashable: Any = scopeOwner) {
        if (scopeOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            throw RuntimeException("It's prohibited to request dependencies if scope owner is already destroyed")
        }

        val lifecycle = scopeOwner.lifecycle
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                trashable.trashValue()
            }
        })
    }

    private fun subscribeOnDestroyable(scopeOwner: Destroyable, trashable: Any = scopeOwner) {
        object : OnDestroyObserver {
            override fun onDestroy() {
                scopeOwner.removeObserver(this)
                trashable.trashValue()
            }
        }.run {
            scopeOwner.addObserver(this)
        }
    }

    private fun Any.trashValue() {
        ownersToValuesMap.remove(this)
    }
}