package dm.uporov.machete.exception

class GenericInDependencyException(type: String) :
    RuntimeException("$type. Cannot to provide dependency with generic type.")