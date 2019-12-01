package dm.uporov.machete.apt.utils

import androidx.lifecycle.LifecycleOwner
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import com.sun.tools.javac.util.Name
import dm.uporov.machete.Destroyable
import dm.uporov.machete.apt.legacy_model.DependencyLegacy
import dm.uporov.machete.apt.model.Dependency
import dm.uporov.machete.exception_legacy.GenericInDependencyException
import dm.uporov.machete.exception_legacy.IncorrectCoreOfScopeException
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName


fun Symbol.asDependency(
    state: Dependency.State
) = Dependency(
    typeName = enclClass().asType().asTypeName().javaToKotlinType(),
    state = state
)




fun Symbol.asDependencyLegacy(parentClassName: Name): DependencyLegacy {
    if (qualifiedName.toString() == "error.NonExistentClass") {
        throw RuntimeException("Wrong $parentClassName class definition")
    }
    return DependencyLegacy(type.asTypeName().javaToKotlinType())
}

fun Symbol.ClassSymbol.asDependencyLegacy(isSinglePerScope: Boolean) =
    asDependencyLegacy(null, isSinglePerScope)

fun Symbol.MethodSymbol.asDependencyLegacy(isSinglePerScope: Boolean) =
    asDependencyLegacy(paramsAsDependencies(), isSinglePerScope)

private fun Symbol.asDependencyLegacy(params: List<DependencyLegacy>?, isSinglePerScope: Boolean) =
    DependencyLegacy(
        enclClass().asType().asTypeName().javaToKotlinType(),
        isSinglePerScope,
        params
    )

fun Symbol.MethodSymbol.paramsAsDependencies(): List<DependencyLegacy> {
    val params = params()
    if (params.isNullOrEmpty()) return emptyList()

    return params.map { it.asDependencyLegacy(qualifiedName) }
}

fun Type.ClassType.toClassName(): ClassName {
    val type = toString()
    return ClassName(type.substringBeforeLast("."), type.substringAfterLast("."))
}

private const val MODULE_FIELD_NAME_FORMAT = "%sModule"

fun ClassName.moduleName() = MODULE_FIELD_NAME_FORMAT.format(simpleName)

fun ClassName.moduleClassName() = ClassName(packageName, moduleName())

fun TypeName.flatGenerics(): String = toString().flatGenerics()

fun TypeName.javaToKotlinType(): TypeName =
    if (this is ParameterizedTypeName) {
        (rawType.javaToKotlinType() as ClassName).parameterizedBy(
            *typeArguments.map(TypeName::javaToKotlinType).toTypedArray()
        )
    } else {
        JavaToKotlinClassMap.INSTANCE
            .mapJavaToKotlin(FqName(toString()))
            ?.asSingleFqName()
            ?.asString()
            ?.let(ClassName.Companion::bestGuess)
            ?: this
    }

private fun String.flatGenerics(): String {
    val qualifiedName = substringBefore("<")
    if (!qualifiedName.contains(".")) throw GenericInDependencyException(
        this
    )

    val name = qualifiedName.substringAfterLast(".")
    val intoGeneric = substringAfter("<", "").substringBeforeLast(">", "")
    return if (intoGeneric.isBlank()) {
        name
    } else {
        try {
            "$name${intoGeneric.split(",").joinToString(separator = "") { it.flatGenerics() }}"
        } catch (e: GenericInDependencyException) {
            throw GenericInDependencyException(this)
        }
    }
}

fun Set<Pair<Int, DependencyLegacy>>.toGroupedMap() = asSequence()
    .groupBy(Pair<Int, DependencyLegacy>::first) { it.second }
    .mapValues { it.value.toSet() }

fun Symbol.ClassSymbol.checkOnDestroyable() {
    if (!isImplementedOneOfInterfaces(LifecycleOwner::class, Destroyable::class)) {
        throw IncorrectCoreOfScopeException(className())
    }
}

private fun Symbol.ClassSymbol.isImplementedOneOfInterfaces(vararg interfacesToImpl: KClass<*>): Boolean {
    if (interfaces.nonEmpty()) {
        interfaces.find { it.isKindOfOneOf(*interfacesToImpl) }?.run { return true }
    }

    if (superclass == Type.noType) return false

    return (superclass.tsym as? Symbol.ClassSymbol)?.isImplementedOneOfInterfaces(*interfacesToImpl)
        ?: false
}

private fun Type.isKindOfOneOf(vararg cls: KClass<*>): Boolean {
    if (this !is Type.ClassType) return false

    cls.forEach {
        if (toClassName() == it.asClassName()) return true
    }

    return interfaces_field?.find { isKindOfOneOf(*cls) } != null
}