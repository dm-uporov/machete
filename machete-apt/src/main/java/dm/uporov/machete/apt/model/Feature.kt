package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

data class Feature(
    val coreClass: Symbol.TypeSymbol,
    // TODO includeFeatures
//    val includeFeatures: Set<Symbol.TypeSymbol>,
    val childFeatures: Set<Feature>,
    val dependencies: List<Symbol.TypeSymbol>
)