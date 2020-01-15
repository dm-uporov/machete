package dm.uporov.machete.extensions

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import dm.uporov.machete.provider.just
import dm.uporov.machete.provider.parentProvider

fun <F, P : Application> P.applicationAsParentProvider() =
    parentProvider<F, P>({ true }, just { this })

inline fun <F : Fragment, reified P : Activity> activityAsParentProvider() = parentProvider<F, P>(
    { it.activity is P },
    { it.activity as P }
)