package dm.uporov.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core_analytics_api.Analytics
import com.example.core_analytics_api.Event
import dm.uporov.machete.annotation.MacheteFeature
import dm.uporov.repository_items.ItemsRepositoryCore
import dm.uporov.repository_items_api.Item

@MacheteFeature(
    modules = [ItemsRepositoryCore::class],
    dependencies = [Analytics::class, Context::class]
)
class ListFragment : Fragment(), ListView {

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
}