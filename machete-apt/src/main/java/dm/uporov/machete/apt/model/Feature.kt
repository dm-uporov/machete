package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

data class Feature(
    val coreClass: Symbol.TypeSymbol,
    val modules: Set<Module>,
    val features: Set<Feature>,
    val dependencies: List<Symbol.TypeSymbol>,
    val internalDependencies: List<Symbol.TypeSymbol>
) {
    val scopeDependencies = (dependencies + internalDependencies).distinct()
}