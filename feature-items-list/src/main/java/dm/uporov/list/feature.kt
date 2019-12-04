package dm.uporov.list

import dm.uporov.list.ListActivityComponentDefinition.Companion.listActivityComponentDefinition
import dm.uporov.machete.provider.single

val listActivityComponentDefinition = listActivityComponentDefinition(
    single { ListPresenterImpl(it, it.getAnalytics()) }
)