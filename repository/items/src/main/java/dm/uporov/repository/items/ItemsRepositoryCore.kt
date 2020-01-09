package dm.uporov.repository.items

import android.content.Context
import dm.uporov.machete.annotation.MacheteModule
import dm.uporov.machete.provider.single
import dm.uporov.repository_items.ItemsRepositoryCoreModuleDefinition.Companion.itemsRepositoryCoreModuleDefinition
import dm.uporov.repository.items.api.ItemsRepository
import dm.uporov.repository.items.impl.ItemsRepositoryImpl

@MacheteModule(
    api = [ItemsRepository::class],
    required = [Context::class]
)
object ItemsRepositoryCore

val itemsRepositoryModule = itemsRepositoryCoreModuleDefinition(
    single { ItemsRepositoryImpl(it.getContext()) }
)