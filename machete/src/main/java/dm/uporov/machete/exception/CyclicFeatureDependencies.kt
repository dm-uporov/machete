package dm.uporov.machete.exception

class CyclicFeatureDependencies(featuresNames: List<String>) :
    RuntimeException("Cannot to build features graph. Try to check features $featuresNames")