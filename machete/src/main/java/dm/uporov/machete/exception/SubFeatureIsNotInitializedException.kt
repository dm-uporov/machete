package dm.uporov.machete.exception

import kotlin.reflect.KClass

class SubFeatureIsNotInitializedException(owner: KClass<*>) :
    RuntimeException("You must initialize the sub-feature by invoke 'inject(${owner.java.canonicalName})'")