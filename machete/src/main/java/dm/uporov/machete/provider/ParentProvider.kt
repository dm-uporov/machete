package dm.uporov.machete.provider

interface ParentProvider<FEATURE, PARENT> : Provider<FEATURE, PARENT> {
    val isRelevantFor: (FEATURE) -> Boolean
}

fun <FEATURE, PARENT> parentProvider(
    isRelevantFor: (FEATURE) -> Boolean,
    parentProvider: (FEATURE) -> PARENT
) = SimpleParentProvider(isRelevantFor, just(parentProvider))

class SimpleParentProvider<FEATURE, PARENT> internal constructor(
    override val isRelevantFor: (FEATURE) -> Boolean,
    private val parentProvider: Provider<FEATURE, PARENT>
) : ParentProvider<FEATURE, PARENT>, Provider<FEATURE, PARENT> by parentProvider
