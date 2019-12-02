package dm.uporov.machete.apt.utils

import androidx.lifecycle.LifecycleOwner
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import dm.uporov.machete.Destroyable
import dm.uporov.machete.exception_legacy.GenericInDependencyException
import dm.uporov.machete.exception_legacy.IncorrectCoreOfScopeException
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

fun Type.ClassType.toClassName(): ClassName {
    val type = toString()
    return ClassName(type.substringBeforeLast("."), type.substringAfterLast("."))
}

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

fun List<Attribute.Class>?.toTypeSymbols(): Sequence<Symbol.TypeSymbol> {
    this ?: return emptySequence()

    return this.asSequence().map { it.classType.asElement() }
}

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