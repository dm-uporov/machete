package dm.uporov.machete.apt

import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Symbol
import dm.uporov.machete.apt.model.ApplicationMirror
import dm.uporov.machete.apt.model.FeatureMirror
import javax.lang.model.element.Element


fun Element.toApplicationMirror(): ApplicationMirror? {
    if (this !is Symbol.ClassSymbol) return null

    var includesFeatures: List<String> = emptyList()
    var childFeatures: List<String> = emptyList()

    val coreClass = enclClass().asType().asTypeName().javaToKotlinType()
    for (annotation in annotationMirrors) {
        for (pair in annotation.values) {
            when (pair.fst.simpleName.toString()) {
                "includesFeatures" -> includesFeatures = (pair.snd.value as Array<String>).toList()
                "childFeatures" -> childFeatures = (pair.snd.value as Array<String>).toList()
                // TODO "dependencies"
            }
        }
    }

    return ApplicationMirror(
        coreClass,
        childFeatures,
        includesFeatures,
        // TODO dependencies
        emptyList()
    )
}

internal fun Element.toFeatureMirror(): FeatureMirror? {
    if (this !is Symbol.ClassSymbol) return null

    var name: String? = null
    var includesFeatures: List<String> = emptyList()
    var childFeatures: List<String> = emptyList()

    val coreClass = enclClass().asType().asTypeName().javaToKotlinType()
    for (annotation in annotationMirrors) {
        for (pair in annotation.values) {
            when (pair.fst.simpleName.toString()) {
                "name" -> name = pair.snd.value as String
                "includesFeatures" -> includesFeatures = (pair.snd.value as Array<String>).toList()
                "childFeatures" -> childFeatures = (pair.snd.value as Array<String>).toList()
                // TODO "dependencies"
            }
        }
    }

    return FeatureMirror(
        coreClass,
        name ?: return null,
        includesFeatures,
        childFeatures,
        // TODO dependencies
        emptyList()
    )
}

