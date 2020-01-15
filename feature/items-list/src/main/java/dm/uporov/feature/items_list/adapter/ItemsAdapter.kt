package dm.uporov.feature.items_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dm.uporov.feature.items_list.ListFragment
import dm.uporov.feature.items_list.channel.AddToFavoritesClickChannel
import dm.uporov.feature.items_list.channel.RemoveFromFavoritesClickChannel
import dm.uporov.list.R
import dm.uporov.machete.annotation.FeatureScope
import dm.uporov.repository.items.api.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@FeatureScope(ListFragment::class)
class ItemsAdapter(
    private val coroutineScope: CoroutineScope,
    private val addToFavoritesClickChannel: AddToFavoritesClickChannel,
    private val removeFromFavoritesClickChannel: RemoveFromFavoritesClickChannel
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
                coroutineScope.launch { addToFavoritesClickChannel.send(itemsList[position]) }
            }
        }
        vh.onRemoveClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                coroutineScope.launch { removeFromFavoritesClickChannel.send(itemsList[position]) }
            }
        }
        return vh
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }
}