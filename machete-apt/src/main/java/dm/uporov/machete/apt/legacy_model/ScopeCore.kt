package dm.uporov.machete.apt.legacy_model

import com.squareup.kotlinpoet.ClassName

data class ScopeCore(
    val scopeId: Int,
    val parentScopeId: Int?,
    val coreClass: ClassName,
    val requestedDependencies: Set<DependencyLegacy>
)