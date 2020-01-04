package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

internal data class Feature(
    val coreClass: Symbol.TypeSymbol,
    val modules: Set<Module>,
    val features: Set<Feature>,
    val required: List<Symbol.TypeSymbol>,
    val internalDependencies: List<Symbol.TypeSymbol>
) {
    val scopeDependencies = required
        .plus(internalDependencies)
        .plus(modules.map(Module::api).flatten())
        .plus(modules.map(Module::required).flatten())
        .distinct()
}