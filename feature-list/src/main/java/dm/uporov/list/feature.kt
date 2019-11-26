package dm.uporov.list

import dm.uporov.list.di.ListActivityComponentDefinition.Companion.listActivityComponentDefinition
import dm.uporov.list.di.getAnalytics
import dm.uporov.machete.provider.single

const val LIST_FEATURE = "list"

val listActivityComponentDefinition = listActivityComponentDefinition(
    single { ListPresenterImpl(it, it.getAnalytics()) }
)