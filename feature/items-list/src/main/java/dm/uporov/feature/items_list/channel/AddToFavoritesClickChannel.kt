package dm.uporov.feature.items_list.channel

import dm.uporov.repository.items.api.Item
import kotlinx.coroutines.channels.Channel

class AddToFavoritesClickChannel(channel: Channel<Item> = Channel()) : Channel<Item> by channel