package dm.uporov.machete.apt.legacy_model

import com.squareup.kotlinpoet.TypeName
import dm.uporov.machete.apt.flatGenerics

data class DependencyLegacy constructor(
    val typeName: TypeName,
    val isSinglePerScope: Boolean = true,
    val params: List<DependencyLegacy>? = null
) {

    val uniqueName: String = typeName.flatGenerics()

    override fun equals(other: Any?): Boolean {
        if (other !is DependencyLegacy) return false
        return typeName == other.typeName
    }

    override fun hashCode(): Int {
        return typeName.hashCode()
    }
}