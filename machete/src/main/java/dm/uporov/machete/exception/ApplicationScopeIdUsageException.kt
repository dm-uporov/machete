package dm.uporov.machete.exception

class ApplicationScopeIdUsageException(scopeId: Int, className: String) : RuntimeException(
    "scopeId = $scopeId is used by application scope. Choose another scopeId for $className."
)