package dm.uporov.feature.items_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dm.uporov.list.R
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.repository_items_api.Item

@FeatureScope(ListFragment::class)
class ItemsAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    private val itemsList = mutableListOf<Item>()

    fun updateItems(items: List<Item>) {
        itemsList.clear()
        itemsList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = itemsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }
}