package dm.uporov.machete.exception

class ClassIsNotAnnotatedException(featureClassName: String, annotationName: String) :
    RuntimeException("$featureClassName is not annotated with $annotationName")