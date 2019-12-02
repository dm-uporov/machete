package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

data class Module(
    val coreClass: Symbol.TypeSymbol,
    val modules: Set<Module>,
    val provideDependencies: List<Symbol.TypeSymbol>,
    val dependencies: List<Symbol.TypeSymbol>,
    val internalDependencies: List<Symbol.TypeSymbol>
) {
    val scopeDependencies = (provideDependencies + internalDependencies).toSet()
}