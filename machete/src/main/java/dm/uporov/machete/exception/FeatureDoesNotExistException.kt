package dm.uporov.machete.exception

class FeatureDoesNotExistException(featureName: String) :
    RuntimeException("There is no feature with name: $featureName")