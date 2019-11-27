package dm.uporov.machete.exception

import dm.uporov.machete.annotation.MacheteApplication

class MoreThanOneMacheteApplicationException
    : RuntimeException("Annotation ${MacheteApplication::class.java.canonicalName} cannot be used for more than one class")