package dm.uporov.machete.exception

class NoScopeIdException(className: String): RuntimeException("You have to provide scopeId for $className")