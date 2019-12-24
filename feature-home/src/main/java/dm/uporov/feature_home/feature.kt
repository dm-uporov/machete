package dm.uporov.feature_home

import dm.uporov.list.listFragmentComponentDefinition
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.parentProvider

val homeActivityComponentDefinition =
    HomeActivityComponentDefinition.homeActivityComponentDefinition(
        listFragmentParentProvider = parentProvider({ it.activity is HomeActivity }, { it.activity as HomeActivity }),
        listFragmentComponentDefinition = listFragmentComponentDefinition
    )