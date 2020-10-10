package org.http4k.connect.storage

/**
 * Storage for a set of objects keyed by String
 */
interface Storage<T : Any> {
    operator fun get(key: String): T?
    operator fun set(key: String, data: T)
    fun create(key: String, data: T): Boolean
    fun update(key: String, data: T): Boolean
    fun remove(key: String): Boolean
    fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T): Set<T>
    fun removeAll(keyPrefix: String = ""): Boolean

    companion object
}
