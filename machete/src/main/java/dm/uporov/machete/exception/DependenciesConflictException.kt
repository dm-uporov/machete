package dm.uporov.machete.exception

class DependenciesConflictException(className: String) :
    RuntimeException("There is more than one provider for $className")