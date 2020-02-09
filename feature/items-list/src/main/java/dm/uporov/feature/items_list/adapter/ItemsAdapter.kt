package dm.uporov.feature.items_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dm.uporov.feature.items_list.ListFragment
import dm.uporov.list.R
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.repository.items.api.Item
import io.reactivex.Observer

@FeatureScope(ListFragment::class)
class ItemsAdapter(
    private val addToFavoritesClicksObserver: Observer<Item>,
    private val removeFromFavoritesClicksObserver: Observer<Item>
) : RecyclerView.Adapter<ItemViewHolder>() {

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

        val vh = ItemViewHolder(view)
        vh.onAddClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                addToFavoritesClicksObserver.onNext(itemsList[position])
            }
        }
        vh.onRemoveClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                removeFromFavoritesClicksObserver.onNext(itemsList[position])
            }
        }
        return vh
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }
}