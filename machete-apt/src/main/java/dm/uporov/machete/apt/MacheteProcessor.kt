package dm.uporov.machete.apt

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Attribute
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import dm.uporov.machete.APPLICATION_SCOPE_ID
import dm.uporov.machete.annotation.*
import dm.uporov.machete.apt.builder.ModuleBuilder
import dm.uporov.machete.apt.legacy_model.*
import dm.uporov.machete.apt.model.*
import dm.uporov.machete.exception.*
import dm.uporov.machete.exception_legacy.ApplicationScopeIdUsageException
import dm.uporov.machete.exception_legacy.DependenciesConflictException
import dm.uporov.machete.exception_legacy.IllegalAnnotationUsageException
import dm.uporov.machete.exception_legacy.NoScopeIdException
import dm.uporov.machete.exception_legacy.SeveralScopesUseTheSameIdException
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(MacheteProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MacheteProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            MacheteApplication::class.java.name
//            ,
//            ApplicationScope::class.java.name,
//            MacheteFeature::class.java.name
//            ,
//            FeatureScope::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(
        set: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment
    ): Boolean {
//        val applicationMirror = roundEnvironment.getApplicationMirror() ?: return true
//        val featuresMirrors = roundEnvironment.getFeaturesMirrors().toMutableSet()
        val app =
            roundEnvironment.getElementsAnnotatedWith(MacheteApplication::class.java).first() as Symbol.ClassSymbol

        val appFeature = app.asFeature(MacheteApplication::class)

        appFeature.childFeatures

//        val features: Set<Attribute.Class> = includeFeatures + childFeatures
//
//        if (features.size > includeFeatures.size + childFeatures.size) {
//            val duplicates = includeFeatures.intersect(childFeatures)
//            throw FeatureIsChildAndIncludedException(duplicates.map {
//                it.classType.asElement().qualifiedName.toString()
//            })
//        }
//
//        features.forEach { feature ->
//            val featureElement = feature.classType.asElement()
//            val featureAnnotation = featureElement.annotationMirrors.find {
//                (it.annotationType as Type.ClassType).toClassName().canonicalName == MacheteFeature::class.java.canonicalName
//            }
//            if (featureAnnotation == null) {
//                throw ClassIsNotAnnotatedException(featureElement.qualifiedName.toString())
//            } else {
//                // TODO берем dependencies фичи, и реализуем AppComponent как DependencyResolver
//            }
//        }
//
//        val independentFeatures =
//            featuresMirrors.filter { it.childFeatures.isEmpty() && it.includesFeatures.isEmpty() }
//
//        val features = if (independentFeatures.isEmpty()) {
//            if (featuresMirrors.isEmpty()) {
//                // all right, just no features are defined
//                emptySet()
//            } else {
//                throw CyclicFeatureDependenciesException(featuresMirrors.map(FeatureMirror::name))
//            }
//        } else {
//            featuresMirrors
//                .subtract(independentFeatures)
//                .resolveFeatures(
//                    independentFeatures.map { it.reifie(emptyList(), emptyList()) }.toSet()
//                )
//        }
//
//        val application = applicationMirror.resolveFeatures(features)
//
//
//        val applicationScopeDependencies: List<Dependency> = roundEnvironment
//            .getScopeLevelDependencies(ApplicationScope::class)
//            .map { it.second }
//        val featuresScopeDependencies: Set<Pair<String?, Dependency>> = roundEnvironment
//            .getScopeLevelDependencies(FeatureScope::class)
//
//
//        val applicationClassName = application.toClassName()
//        val rootScope = roundEnvironment.generateRootScope(application, applicationClassName)
//        val scopesCores = roundEnvironment.generateScopesBy(
//            coreMarker = LegacyMacheteFeature::class,
//            scopeLevelMarker = LegacyFeatureScope::class,
//            rootClassName = applicationClassName,
//            rootDependencies = rootScope.providedDependencies
//        )
//
//        DakkerBuilder(application.toClassName(), scopesCores).build().write()
        return true
    }

    private fun RoundEnvironment.getScopeLevelDependencies(
        scopeLevelMarker: KClass<out Annotation>
    ): Set<Pair<String?, Dependency>> {
        val scopeLevelDependencies = mutableSetOf<Pair<String?, Dependency>>()

        getElementsAnnotatedWith(scopeLevelMarker.java)?.forEach { element ->
            if (element !is Symbol) return@forEach

            val dependencyScopeName = element.getDependencyScopeName()

            when (element) {
                is Symbol.MethodSymbol -> TODO()
//                element
//                    .asDependency(isSinglePerScope)
//                    .let { scopeLevelDependencies.add(scopeId to it) }
//                    .let { wasProviderAddedToCollection(it, element.enclClass()) }
                is Symbol.ClassSymbol -> element
                    .asDependency(Dependency.State.NEED_FOR_PROVIDE)
                    .also { scopeLevelDependencies.add(dependencyScopeName to it) }
            }
        }

        return scopeLevelDependencies
    }

    private fun Symbol.getDependencyScopeName(): String? {
        for (annotation in annotationMirrors) {
            for (pair in annotation.values) {
                when (pair.fst.simpleName.toString()) {
                    "feature" -> return pair.snd.value as String
                }
            }
        }
        return null
    }


    private fun RoundEnvironment.getScopeLevelDependenciesLegacy(scopeLevelMarker: KClass<out Annotation>): ScopeLevelDependencies {
        val scopeLevelDependencies = mutableSetOf<Pair<Int, DependencyLegacy>>()
        val scopeLevelDependenciesWithoutProviders = mutableSetOf<Pair<Int, DependencyLegacy>>()

        getElementsAnnotatedWith(scopeLevelMarker.java)?.forEach { element ->
            if (element !is Symbol) return@forEach

            val (scopeId, isSinglePerScope) = element.getDependencyInfoLegacy()

            when (element) {
                is Symbol.MethodSymbol -> element
                    .asDependencyLegacy(isSinglePerScope)
                    .let { scopeLevelDependencies.add(scopeId to it) }
                    .let { wasProviderAddedToCollection(it, element.enclClass()) }
                is Symbol.ClassSymbol -> element
                    .asDependencyLegacy(isSinglePerScope)
                    .let { scopeLevelDependenciesWithoutProviders.add(scopeId to it) }
                    .let { wasProviderAddedToCollection(it, element) }
            }
        }

        return ScopeLevelDependencies(
            scopeLevelDependencies.toGroupedMap(),
            scopeLevelDependenciesWithoutProviders.toGroupedMap()
        )
    }

    private fun RoundEnvironment.generateRootScope(
        root: Element,
        rootClassName: ClassName
    ): Scope {
        val rootLevelDependencies = getScopeLevelDependenciesLegacy(ApplicationScope::class)

        return mapOf(
            APPLICATION_SCOPE_ID to ScopeCore(
                APPLICATION_SCOPE_ID,
                null,
                rootClassName,
                (root as Symbol.ClassSymbol).getRequestedDependencies()
            )
        )
            .buildGraph(
                scopeId = APPLICATION_SCOPE_ID,
                parentScopeId = null,
                rootClassName = rootClassName,
                coreClass = rootClassName,
                scopeLevelDependencies = rootLevelDependencies,
                parentDependencies = emptySet()
            )
            .let { Scope(rootClassName, it) }
    }

    private fun RoundEnvironment.generateScopesBy(
        coreMarker: KClass<out Annotation>,
        scopeLevelMarker: KClass<out Annotation>,
        rootClassName: ClassName,
        rootDependencies: Set<DependencyLegacy>
    ): Set<ClassName> {
        val scopeLevelDependencies = getScopeLevelDependenciesLegacy(scopeLevelMarker)

        (getElementsAnnotatedWith(coreMarker.java) ?: emptySet())
            .asSequence()
            .map {
                // Core of scope must be class
                if (this !is Symbol.ClassSymbol) {
                    throw IllegalAnnotationUsageException(coreMarker)
                }
                this as Symbol.ClassSymbol
            }
            // Core of scope must implement Destroyable or LifecycleOwner, to Machete can trash all scope onDestroy event
            .onEach(Symbol.ClassSymbol::checkOnDestroyable)
            .map {
                val coreClassName = it.toClassName()
                val (scopeId, parentScopeId) = it.getScopeCoreInfo()
                val requestedDependencies = it.getRequestedDependencies()

                ScopeCore(
                    scopeId,
                    parentScopeId,
                    coreClassName,
                    requestedDependencies
                )
            }
            .also {
                val cores = it.map { core -> core.scopeId to core }.toMap()
                if (it.count() > cores.count()) {
                    val ids = it.map { core -> core.scopeId }.toMutableList()
                    cores.keys.forEach { id ->
                        ids.remove(id)
                    }
                    throw SeveralScopesUseTheSameIdException(ids.first())
                }
                cores.asSequence()
                    .filter { entry -> entry.value.parentScopeId == APPLICATION_SCOPE_ID }
                    .forEach { (scopeId, core) ->
                        cores.buildGraph(
                            scopeId = scopeId,
                            parentScopeId = core.parentScopeId,
                            coreClass = core.coreClass,
                            rootClassName = rootClassName,
                            scopeLevelDependencies = scopeLevelDependencies,
                            parentDependencies = rootDependencies
                        )
                    }
            }
            .map(ScopeCore::coreClass)
            .toSet()
            .run { return this }
    }

    private fun Map<Int, ScopeCore>.buildGraph(
        scopeId: Int,
        parentScopeId: Int? = null,
        coreClass: ClassName,
        rootClassName: ClassName,
        scopeLevelDependencies: ScopeLevelDependencies,
        parentDependencies: Set<DependencyLegacy>
    ): Set<DependencyLegacy> {
        val children = filter { it.value.parentScopeId == scopeId }
        val coreScope = get(scopeId) ?: throw RuntimeException("Something went wrong")

        val scopeDependencies = scopeLevelDependencies.withProviders[scopeId] ?: emptySet()
        val scopeDependenciesWithoutProviders =
            scopeLevelDependencies.withoutProviders[scopeId] ?: emptySet()
        val requestedDependencies = coreScope.requestedDependencies

        val requestedAsParamsDependencies = scopeDependencies
            .asSequence()
            .map { it.params ?: emptyList() }
            .flatten()
            .filter {
                !requestedDependencies.contains(it) &&
                        !parentDependencies.contains(it) &&
                        !scopeDependenciesWithoutProviders.contains(it)
            }
            .toSet()

        val thisScopeDependencies: Set<DependencyLegacy> = coreScope.requestedDependencies
            .union(scopeDependenciesWithoutProviders)
            .union(scopeDependencies)
            .union(parentDependencies)
            .union(requestedAsParamsDependencies)

        val dependenciesWithoutProviders: Set<DependencyLegacy> = coreScope.requestedDependencies
            .union(scopeDependenciesWithoutProviders)
            .subtract(scopeDependencies)
            .subtract(parentDependencies)
            .union(requestedAsParamsDependencies)

        ModuleBuilder(
            coreClassName = coreClass,
            parentCoreClassName = get(parentScopeId)?.coreClass ?: rootClassName,
            rootClassName = rootClassName,
            allDependencies = thisScopeDependencies,
            parentDependencies = parentDependencies,
            dependenciesWithoutProviders = dependenciesWithoutProviders,
            scopeDependencies = scopeDependencies,
            requestedDependencies = requestedDependencies
        ).build().write()

        children.forEach { (scopeId, core) ->
            buildGraph(
                scopeId = scopeId,
                parentScopeId = core.parentScopeId,
                coreClass = core.coreClass,
                rootClassName = rootClassName,
                scopeLevelDependencies = scopeLevelDependencies,
                parentDependencies = thisScopeDependencies
            )
        }
        return thisScopeDependencies
    }

    private fun wasProviderAddedToCollection(wasAdded: Boolean, element: Symbol.ClassSymbol) {
        if (!wasAdded) throw DependenciesConflictException(
            element.qualifiedName.toString()
        )
    }

    private fun Symbol.ClassSymbol.getRequestedDependencies(): Set<DependencyLegacy> {
        return enclosedElements
            .asSequence()
            .filter { it.getAnnotation(LegacyInject::class.java) != null }
            .map {
                DependencyLegacy(it.type.returnType.asTypeName().javaToKotlinType())
            }
            .toSet()
    }

    private fun FileSpec.write() =
        writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]))

    private fun Symbol.getDependencyInfoLegacy(): DependencyInfoLegacy {
        var scopeId: Int? = null
        var isSinglePerScope: Boolean? = null
        for (annotation in annotationMirrors) {
            for (pair in annotation.values) {
                when (pair.fst.simpleName.toString()) {
                    "scopeId" -> scopeId = pair.snd.value as? Int
                    "isSinglePerScope" -> isSinglePerScope = (pair.snd.value as? Boolean)
                        ?: throw RuntimeException("Incorrect value used as isSinglePerScope")
                }
            }
        }
        return DependencyInfoLegacy(
            scopeId ?: APPLICATION_SCOPE_ID,
            isSinglePerScope ?: true
        )
    }

    private fun Symbol.getScopeCoreInfo(): ScopeCoreInfo {
        var scopeId: Int? = null
        var parentScopeId: Int? = null
        for (annotation in annotationMirrors) {
            for (pair in annotation.values) {
                when (pair.fst.simpleName.toString()) {
                    "scopeId" -> scopeId = pair.snd.value as? Int
                    "parentScopeId" -> parentScopeId = pair.snd.value as? Int
                }
            }
        }
        scopeId ?: throw NoScopeIdException(qualifiedName.toString())
        if (scopeId == APPLICATION_SCOPE_ID) throw ApplicationScopeIdUsageException(
            scopeId,
            qualifiedName.toString()
        )

        return ScopeCoreInfo(scopeId, parentScopeId ?: APPLICATION_SCOPE_ID)
    }

    override fun hashCode(): Int {
        return 1
    }
}
