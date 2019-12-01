package dm.uporov.machete.apt.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element

fun Element.toClassName() = ClassName.bestGuess(asType().asTypeName().toString())
