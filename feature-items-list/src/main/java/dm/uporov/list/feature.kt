package dm.uporov.list

import dm.uporov.list.generated.ListActivityComponentDefinition.Companion.listActivityComponentDefinition
import dm.uporov.list.generated.getAnalytics
import dm.uporov.machete.provider.single

const val LIST_FEATURE = "list"

val listActivityComponentDefinition = listActivityComponentDefinition(
    single { ListPresenterImpl(it, it.getAnalytics()) }
)