package dm.uporov.machete.exception_legacy

class NoScopeIdException(className: String): RuntimeException("You have to provide scopeId for $className")