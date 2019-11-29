package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

data class Feature(
    val coreClass: Symbol.TypeSymbol,
    val includeFeatures: Set<Feature>,
    val childFeatures: Set<Feature>,
    val dependencies: List<Symbol.TypeSymbol>
)