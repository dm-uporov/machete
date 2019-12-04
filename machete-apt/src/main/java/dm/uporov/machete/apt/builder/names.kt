package dm.uporov.machete.apt.builder

import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.apt.utils.flatGenerics

internal fun String.asProviderName() = "%sProvider".format(this).decapitalize()
internal fun String.asResolverClassName() = "%s_Resolver".format(this)
internal fun String.asGetterName() = "get%s".format(this)

internal fun String.asModuleClassName() = "%s_Module".format(this)
internal fun String.asModuleDependenciesClassName() = "%s_ModuleDependencies".format(this)
internal fun String.asModuleDefinitionClassName() = "%s_ModuleDefinition".format(this)

internal fun String.asComponentClassName() = "%s_Component".format(this)
internal fun String.asComponentDependenciesClassName() = "%s_ComponentDependencies".format(this)
internal fun String.asComponentDefinitionClassName() = "%s_ComponentDefinition".format(this)

internal fun String.providerFrom(from: String) = "${this.decapitalize()}From${from}Provider"
internal fun Symbol.providerName() = asType().asTypeName().flatGenerics().asProviderName()