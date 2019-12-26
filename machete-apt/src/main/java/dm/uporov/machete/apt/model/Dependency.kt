package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

internal data class Dependency(
    val dependencyClass: Symbol.TypeSymbol,
    val featureClass: Symbol.TypeSymbol
)