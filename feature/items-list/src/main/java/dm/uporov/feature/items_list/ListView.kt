package dm.uporov.feature.items_list

import dm.uporov.repository.items.api.Item

interface ListView {

    fun showItems(items: List<Item>)
}