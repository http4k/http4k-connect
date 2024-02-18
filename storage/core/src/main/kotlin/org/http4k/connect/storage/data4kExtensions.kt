package org.http4k.connect.storage

import dev.forkhandles.data.DataContainer

/**
 * Storage-based implementation of the DataContainer
 */
open class StorageDataContainer(storage: Storage<Any>) :
    DataContainer<Storage<Any>>(storage, { content, it -> content[it] != null },
        { data, it -> data[it] },
        { data, name, value ->
            when (value) {
                null -> data.remove(name)
                else -> data[name] = value
            }
        }
    )
