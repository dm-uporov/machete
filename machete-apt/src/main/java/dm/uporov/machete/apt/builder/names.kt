package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.apt.utils.flatGenerics

internal fun String.asProviderName() = "%sProvider".format(this).decapitalize()
internal fun String.asResolverClassName() = "%sResolver".format(this)
internal fun String.asGetterName() = "get%s".format(this)
internal fun String.asSetterName() = "set%s".format(this)

internal fun String.asModuleClassName() = "%sModule".format(this)
internal fun String.asModuleDependenciesClassName() = "%sModuleDependencies".format(this)
internal fun String.asModuleDefinitionClassName() = "%sModuleDefinition".format(this)

internal fun String.asComponentClassName() = "%sComponent".format(this)
internal fun String.asComponentDependenciesClassName() = "%sComponentDependencies".format(this)
internal fun String.asComponentDefinitionClassName() = "%sComponentDefinition".format(this)

internal fun String.providerFrom(from: String) = "${this.decapitalize()}From${from}Provider"
internal fun Symbol.providerName() = asType().asTypeName().flatGenerics().asProviderName()