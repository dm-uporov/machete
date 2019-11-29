package dm.uporov.machete.apt.model

import com.sun.tools.javac.code.Symbol

data class ScopeDependency(
    val dependencyClass: Symbol.TypeSymbol,
    val featureClass: Symbol.TypeSymbol
)