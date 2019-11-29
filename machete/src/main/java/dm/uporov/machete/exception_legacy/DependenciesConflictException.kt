package dm.uporov.machete.exception_legacy

class DependenciesConflictException(className: String) :
    RuntimeException("There is more than one provider for $className")