package dm.uporov.machete.provider

@FunctionalInterface
interface Provider<O, T> : (O) -> T

fun <NO, OO, T> Provider<OO, T>.mapOwner(oldOwnerProvider: Provider<NO, OO>): Provider<NO, T> {
    return object : Provider<NO, T> {
        override fun invoke(newOwner: NO): T {
            return oldOwnerProvider(newOwner).let(this@mapOwner::invoke)
        }
    }
}