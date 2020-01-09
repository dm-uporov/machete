package dm.uporov.feature.items_list

import dm.uporov.repository_items_api.Item

interface ListView {

    fun showItems(items: List<Item>)
}