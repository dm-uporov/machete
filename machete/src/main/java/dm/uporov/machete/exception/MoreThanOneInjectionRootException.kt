package dm.uporov.machete.exception

import dm.uporov.machete.annotation.LegacyMacheteApplication

class MoreThanOneInjectionRootException
    : RuntimeException("Annotation ${LegacyMacheteApplication::class.java.canonicalName} cannot be used for more than one class")