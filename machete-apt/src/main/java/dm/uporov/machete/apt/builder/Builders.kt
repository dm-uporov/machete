package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*


fun TypeSpec.Builder.withConstructorWithProperties(
    propertiesNamesWithTypes: Iterable<Pair<String, TypeName>>,
    vararg constructorModifiers: KModifier
) = apply {
    val constructorBuilder = FunSpec.constructorBuilder()
        .addModifiers(*constructorModifiers)

    propertiesNamesWithTypes
        .forEach { (providerName, providerType) ->
            constructorBuilder.addParameter(
                ParameterSpec.builder(providerName, providerType)
                    .build()
            )
            addProperty(
                PropertySpec.builder(providerName, providerType)
                    .initializer(providerName)
                    .build()
            )
        }

    primaryConstructor(constructorBuilder.build())
}


fun TypeSpec.Builder.withSimpleInitialCompanion(
    ownerName: String,
    returns: TypeName,
    propertiesNamesWithTypes: Iterable<Pair<String, TypeName>>
) = apply {
    addType(
        TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder(ownerName.decapitalize())
                    .returns(returns)
                    .apply {
                        addParameters(propertiesNamesWithTypes.map { (name, type) ->
                            ParameterSpec.builder(name, type).build()
                        })
                        addStatement(
                            """
                            return $ownerName(
                            ${propertiesNamesWithTypes.joinToString { (name, _) -> "$name = $name" }}
                            )
                            """.trimIndent()
                        )
                    }
                    .build()
            )
            .build()
    )
}