package dm.uporov.machete.apt.utils

import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.apt.model.Module
import dm.uporov.machete.exception.ClassIsNotAnnotatedException

internal fun Symbol.TypeSymbol.asModuleRecursive(internalDependencies: List<Symbol.TypeSymbol>) = asModule(
    internalDependencies = internalDependencies,
    recursive = true
)

internal fun Symbol.TypeSymbol.asModule(internalDependencies: List<Symbol.TypeSymbol>) = asModule(
    internalDependencies = internalDependencies,
    recursive = false
)

private fun Symbol.TypeSymbol.asModule(
    internalDependencies: List<Symbol.TypeSymbol>,
    recursive: Boolean,
    deepRecursive: Boolean = false
): Module {
    val annotationMirror = annotationMirrors.find {
        it.type.asElement().qualifiedName.toString() == MacheteModule::class.qualifiedName
    } ?: throw ClassIsNotAnnotatedException(
        this.qualifiedName.toString(),
        MacheteModule::class.qualifiedName.toString()
    )

    var modulesParam: List<Attribute.Class>? = null
    var apiParam: List<Attribute.Class>? = null
    var implementation: List<Attribute.Class>? = null
    var requiredParam: List<Attribute.Class>? = null

    annotationMirror.values.forEach {
        val name = it.fst.simpleName.toString()
        val value = it.snd.value
        when (name) {
            "modules" -> modulesParam = value as? List<Attribute.Class>
            "required" -> requiredParam = value as? List<Attribute.Class>
            "api" -> apiParam = value as? List<Attribute.Class>
            "implementation" -> implementation = value as? List<Attribute.Class>
        }
    }

    val modules = if (recursive) {
        modulesParam.toTypeSymbols().map {
            it.asModule(
                internalDependencies = emptyList(),
                recursive = deepRecursive,
                deepRecursive = deepRecursive
            )
        }.toSet()
    } else {
        emptySet()
    }

    return Module(
        coreClass = this,
        modules = modules,
        api = apiParam.toTypeSymbols().toList(),
        required = requiredParam.toTypeSymbols().toList(),
        internalDependencies = implementation.toTypeSymbols().toList() + internalDependencies
    )
}