package dm.uporov.machete.apt

import dm.uporov.machete.annotation.MacheteApplication
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.apt.model.*
import dm.uporov.machete.exception.CyclicFeatureDependenciesException
import dm.uporov.machete.exception.FeatureDoesNotExistException
import dm.uporov.machete.exception.MoreThanOneMacheteApplicationException
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element


fun RoundEnvironment.getApplicationMirror(): ApplicationMirror? {
    val annotatedApplications =
        getElementsAnnotatedWith(MacheteApplication::class.java) ?: emptySet()

    val element = when {
        annotatedApplications.isEmpty() -> return null
        else -> annotatedApplications.singleOrNull()
            ?: throw MoreThanOneMacheteApplicationException()
    }

    return element.toApplicationMirror()
}

//fun ApplicationMirror.resolveFeatures(resolvedFeatures: Set<LegacyFeature>): Application {
//    val resolvedIncludes : List<LegacyFeature> = includesFeatures.map { unresolved ->
//        resolvedFeatures.find { it.name == unresolved } ?: throw FeatureDoesNotExistException(unresolved)
//    }
//    val resolvedChild : List<LegacyFeature> = childFeatures.map { unresolved ->
//        resolvedFeatures.find { it.name == unresolved } ?: throw FeatureDoesNotExistException(unresolved)
//    }
//
//    return this.reifie(resolvedIncludes, resolvedChild)
//}

fun RoundEnvironment.getFeaturesMirrors(): Set<FeatureMirror> {
    return (getElementsAnnotatedWith(MacheteFeature::class.java) ?: emptySet())
        .mapNotNull(Element::toFeatureMirror)
        .toSet()
}

//fun Set<FeatureMirror>.resolveFeatures(resolvedFeatures: Set<LegacyFeature>): Set<LegacyFeature> {
//    val resolvedMirrors = mutableSetOf<FeatureMirror>()
//
//    val newResolved = this.mapNotNull { unresolvedFeature ->
//        val resolvedIncludes = unresolvedFeature
//            .includesFeatures
//            .map { includedFeature ->
//                val resolved = resolvedFeatures.find { it.name == includedFeature }
//                resolved ?: return@mapNotNull null
//            }
//
//        val resolvedChild = unresolvedFeature
//            .childFeatures
//            .map { child ->
//                val resolved = resolvedFeatures.find { it.name == child }
//                resolved ?: return@mapNotNull null
//            }
//
//        resolvedMirrors.add(unresolvedFeature)
//        unresolvedFeature.reifie(resolvedIncludes, resolvedChild)
//    }
//
//    return when {
//        resolvedMirrors.isEmpty() -> throw CyclicFeatureDependenciesException(
//            this.map(FeatureMirror::name)
//        )
//        this.size == newResolved.size -> resolvedFeatures + newResolved
//        else -> this.subtract(resolvedMirrors).resolveFeatures(resolvedFeatures + newResolved)
//    }
//}