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
import dm.uporov.feature.items_list.ListFragmentComponentDefinition.Companion.listFragmentComponentDefinition
import dm.uporov.feature.items_list.adapter.ItemsAdapter
import dm.uporov.feature.items_list.channel.AddToFavoritesClickChannel
import dm.uporov.feature.items_list.channel.RemoveFromFavoritesClickChannel
import dm.uporov.list.R
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.machete.provider.single
import dm.uporov.repository.items.api.Item
import dm.uporov.repository.items.api.ItemsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.consumeAsFlow

@FlowPreview
@ExperimentalCoroutinesApi
val listFragmentComponent = listFragmentComponentDefinition(
    coroutineScopeProvider = single { MainScope() },
    itemsAdapterProvider = single {
        ItemsAdapter(
            it.provideCoroutineScope(),
            it.provideAddToFavoritesClickChannel(),
            it.provideRemoveFromFavoritesClickChannel()
        )
    },
    listPresenterProvider = single {
        ListPresenterImpl(
            it,
            it.provideAnalytics(),
            it.provideItemsRepository(),
            it.provideCoroutineScope(),
            it.provideAddToFavoritesClickChannel().consumeAsFlow(),
            it.provideRemoveFromFavoritesClickChannel().consumeAsFlow()
        )
    },
    addToFavoritesClickChannelProvider = single { AddToFavoritesClickChannel() },
    removeFromFavoritesClickChannelProvider = single { RemoveFromFavoritesClickChannel() }
)

@MacheteFeature(
    required = [
        Analytics::class,
        Context::class,
        ItemsRepository::class
    ],
    implementation = [
        CoroutineScope::class,
        AddToFavoritesClickChannel::class,
        RemoveFromFavoritesClickChannel::class
    ]
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