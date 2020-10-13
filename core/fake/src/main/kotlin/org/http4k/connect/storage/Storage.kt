package org.http4k.connect.storage

/**
 * Storage for a set of objects keyed by String
 */

// maybe generify key
interface Storage<T : Any> {
    operator fun get(key: String): T?
    operator fun set(key: String, data: T)
    fun remove(key: String): Boolean

    fun keySet(keyPrefix: String): Set<String>

    fun removeAll(keyPrefix: String = ""): Boolean

    companion object
}
