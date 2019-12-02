package dm.uporov.machete.apt.utils

import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.exception.ClassIsNotAnnotatedException

fun Symbol.TypeSymbol.asModuleRecursive(internalDependencies: List<Symbol.TypeSymbol>) = asModule(
    internalDependencies = internalDependencies,
    recursive = true
)

fun Symbol.TypeSymbol.asModule(internalDependencies: List<Symbol.TypeSymbol>) = asModule(
    internalDependencies = internalDependencies,
    recursive = false
)

private fun Symbol.TypeSymbol.asModule(
    internalDependencies: List<Symbol.TypeSymbol>,
    recursive: Boolean
): Module {
    val annotationMirror = annotationMirrors.find {
        it.type.asElement().qualifiedName.toString() == MacheteModule::class.qualifiedName
    } ?: throw ClassIsNotAnnotatedException(
        this.qualifiedName.toString(),
        MacheteModule::class.qualifiedName.toString()
    )

    var modulesParam: List<Attribute.Class>? = null
    var dependenciesParam: List<Attribute.Class>? = null
    var provideParam: List<Attribute.Class>? = null

    annotationMirror.values.forEach {
        val name = it.fst.simpleName.toString()
        val value = it.snd.value
        when (name) {
            "modules" -> modulesParam = value as? List<Attribute.Class>
            "dependencies" -> dependenciesParam = value as? List<Attribute.Class>
            "provide" -> provideParam = value as? List<Attribute.Class>
        }
    }

    val modules = if (recursive) {
        modulesParam.toTypeSymbols().map { it.asModule(emptyList()) }.toSet()
    } else {
        emptySet()
    }

    return Module(
        coreClass = this,
        modules = modules,
        provideDependencies = provideParam.toTypeSymbols().toList(),
        dependencies = dependenciesParam.toTypeSymbols().toList(),
        internalDependencies = internalDependencies
    )
}