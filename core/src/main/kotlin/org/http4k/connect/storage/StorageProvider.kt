package org.http4k.connect.storage

/**
 * Dynamically creates a Storage for a named "group" of storage
 */
class StorageProvider<T>(private val baseStorage: Storage<T>) : (String) -> Storage<T> {
    override operator fun invoke(p1: String): Storage<T> = object : Storage<T> {
        override fun get(key: String): T? = baseStorage[p1 + key]

        override fun set(key: String, data: T) {
            baseStorage[p1 + key] = data
        }

        override fun create(key: String, data: T): Boolean = baseStorage.create(p1 + key, data)

        override fun update(key: String, data: T): Boolean = baseStorage.update(p1 + key, data)

        override fun remove(key: String): Boolean = baseStorage.remove(p1 + key)

        override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T): Set<T> = baseStorage.keySet(p1 + keyPrefix, decodeFunction)

        override fun removeAll(keyPrefix: String) = baseStorage.removeAll(p1 + keyPrefix)
    }

    companion object
}
