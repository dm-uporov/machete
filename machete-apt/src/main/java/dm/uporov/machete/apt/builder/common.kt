package dm.uporov.machete.apt.builder

private const val PROVIDER_NAME_FORMAT = "%sProvider"

fun String.asProviderName() = PROVIDER_NAME_FORMAT.format(this).decapitalize()