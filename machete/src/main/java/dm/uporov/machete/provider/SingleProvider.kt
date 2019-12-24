package dm.uporov.machete.provider

import java.util.*

// Provider for scope single dependency
fun <O : Any, T> single(provide: (O) -> T) = SingleProvider(provide)

class SingleProvider<O: Any, T> internal constructor(
    private val provider: (O) -> T
) : Provider<O, T> {

    private val weakHashMap = WeakHashMap<O, T>()

    override fun invoke(scopeOwner: O): T {
        synchronized(this) {
            val dependency = weakHashMap[scopeOwner]
            if (dependency == null) {
                val newDependency = provider.invoke(scopeOwner)
                weakHashMap[scopeOwner] = newDependency
                println("TAAAG, ${scopeOwner::class.java.simpleName}")
                return newDependency
            } else {
                return dependency
            }
        }
    }
}

interface MyInterface