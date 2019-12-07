package dm.uporov.feature_home

import dm.uporov.list.listFragmentComponentDefinition
import dm.uporov.machete.provider.just

val homeActivityComponentDefinition = HomeActivityComponentDefinition.homeActivityComponentDefinition(
    homeActivityFromListFragmentProvider = just { it.activity as HomeActivity },
    listFragmentComponentDefinition = listFragmentComponentDefinition
)