package dm.uporov.list

import dm.uporov.list.ListActivity_ComponentDefinition.Companion.listActivity_ComponentDefinition
import dm.uporov.machete.provider.single

val listActivityComponentDefinition = listActivity_ComponentDefinition(
    single { ListPresenterImpl(it, it.getAnalytics()) }
)