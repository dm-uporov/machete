package dm.uporov.feature.items_list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dm.uporov.list.R
import dm.uporov.repository.items.api.Item

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val titleTv: TextView = view.findViewById(R.id.titleTv)
    private val descriptionTv: TextView = view.findViewById(R.id.descriptionTv)
    private val priceTv: TextView = view.findViewById(R.id.priceTv)

    fun bind(item: Item) = with(item) {
        titleTv.text = name
        descriptionTv.text = description
        priceTv.text = price.toString()
    }
}