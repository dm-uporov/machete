package dm.uporov.machete.provider

fun <O, T> factory(provide: (O) -> T) = FactoryProvider(provide)

fun <O, T> just(provide: (O) -> T) = factory(provide)

class FactoryProvider<O, T> internal constructor(
    private val provider: (O) -> T
) : Provider<O, T> {

    override fun invoke(scopeOwner: O): T = provider(scopeOwner)
}