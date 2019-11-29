package dm.uporov.machete.exception

class FeatureIsChildAndIncludedException(featuresClassNames: List<String>) :
    RuntimeException("Features cannot be used as included and child at the same time. Please check:  $featuresClassNames")