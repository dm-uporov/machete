package dm.uporov.feature.items_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dm.uporov.core.analytics.api.Analytics
import dm.uporov.core.analytics.api.Event
import dm.uporov.core.favorites.api.FavoritesInteractor
import dm.uporov.list.*
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.provider.single
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

val listFragmentComponent = ListFragmentComponentDefinition.listFragmentComponentDefinition(
    coroutineScopeProvider = single { MainScope() },
    itemsAdapterProvider = single { ItemsAdapter() },
    listPresenterProvider = single {
        ListPresenterImpl(
            it,
            it.provideAnalytics(),
            it.provideItemsRepository(),
            it.provideCoroutineScope()
        )
    }
)

@MacheteFeature(
    required = [
        Analytics::class,
        Context::class,
        ItemsRepository::class,
        FavoritesInteractor::class
    ],
    implementation = [CoroutineScope::class]
)
class ListFragment : Fragment(), ListView {

    private val coroutineScope by injectCoroutineScope()
    private val listPresenter by injectListPresenter()
    private val analytics by injectAnalytics()
    private val adapter by injectItemsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listPresenter.start()
    }

    override fun showItems(items: List<Item>) {
        adapter.updateItems(items)
        analytics.sendEvent(Event("Items are showed"))
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}