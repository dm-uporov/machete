package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.*


fun TypeSpec.Builder.withConstructorWithProviders(
    providersNamesWithTypes: Iterable<Pair<String, ParameterizedTypeName>>,
    vararg constructorModifiers: KModifier
) = apply {
    val constructorBuilder = FunSpec.constructorBuilder()
        .addModifiers(*constructorModifiers)

    providersNamesWithTypes
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