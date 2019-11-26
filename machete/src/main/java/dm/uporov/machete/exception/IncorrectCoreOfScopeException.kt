package dm.uporov.machete.exception

import androidx.lifecycle.LifecycleOwner

class IncorrectCoreOfScopeException(className: String) :
    RuntimeException("$className. Core of scope must implements ${LifecycleOwner::class.java.canonicalName}")