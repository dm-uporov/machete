package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

internal data class Module(
    val coreClass: Symbol.TypeSymbol,
    val modules: Set<Module>,
    val api: List<Symbol.TypeSymbol>,
    val required: List<Symbol.TypeSymbol>,
    val internalDependencies: List<Symbol.TypeSymbol>
) {
    val scopeDependencies = (api + internalDependencies).distinct()
}