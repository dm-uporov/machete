package dm.uporov.machete.apt.model

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName

internal class ConstructorProperty(
    val name: String,
    val type: TypeName,
    vararg val modifiers: KModifier
)

internal fun Pair<String, TypeName>.asConstructorProperty() =
    ConstructorProperty(first, second)

internal fun ConstructorProperty.asParameter() = ParameterSpec.builder(name, type).build()