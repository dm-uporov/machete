package dm.uporov.machete.exception

import dm.uporov.machete.annotation.MacheteApplication

class MoreThanOneInjectionRootException
    : RuntimeException("Annotation ${MacheteApplication::class.java.canonicalName} cannot be used for more than one class")