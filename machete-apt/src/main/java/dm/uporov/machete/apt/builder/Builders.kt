package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*
import dm.uporov.machete.apt.model.ConstructorProperty
import dm.uporov.machete.apt.model.asParameter

internal fun TypeSpec.Builder.withConstructorWithProperties(
    constructorProperties: Iterable<ConstructorProperty>,
    vararg constructorModifiers: KModifier
) = apply {
    val constructorBuilder = FunSpec.constructorBuilder()
        .addModifiers(*constructorModifiers)

    constructorProperties
        .forEach {
            constructorBuilder.addParameter(
                ParameterSpec.builder(it.name, it.type)
                    .build()
            )
            addProperty(
                PropertySpec.builder(it.name, it.type)
                    .initializer(it.name)
                    .addModifiers(*it.modifiers)
                    .build()
            )
        }

    primaryConstructor(constructorBuilder.build())
}


internal fun TypeSpec.Builder.withSimpleInitialCompanion(
    ownerName: String,
    returns: TypeName,
    constructorProperties: Iterable<ConstructorProperty>
) = apply {
    addType(
        TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder(ownerName.decapitalize())
                    .returns(returns)
                    .apply {
                        addParameters(constructorProperties.map(ConstructorProperty::asParameter))
                        addStatement(
                            """
                            return $ownerName(
                            ${constructorProperties
                                .map(ConstructorProperty::name)
                                .joinToString { "$it = $it" }
                            }
                            )
                            """.trimIndent()
                        )
                    }
                    .build()
            )
            .build()
    )
}