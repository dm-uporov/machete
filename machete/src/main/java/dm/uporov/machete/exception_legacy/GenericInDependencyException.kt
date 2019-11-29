package dm.uporov.machete.exception_legacy

class GenericInDependencyException(type: String) :
    RuntimeException("$type. Cannot to provide dependency with generic type.")