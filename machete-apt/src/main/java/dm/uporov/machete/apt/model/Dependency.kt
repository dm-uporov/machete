package dm.uporov.machete.apt.model

import com.squareup.kotlinpoet.TypeName
import dm.uporov.machete.apt.flatGenerics

data class Dependency(
    val typeName: TypeName,
    val state: State
) {

    val uniqueName: String = typeName.flatGenerics()

    enum class State {
        PROVIDED, NEED_FOR_PROVIDE, REQUESTED
    }
}