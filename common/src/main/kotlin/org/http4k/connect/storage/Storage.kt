package org.http4k.connect.storage

interface Storage<T> {
    operator fun get(key: String): T?
    operator fun set(key: String, data: T)
    fun create(key: String, data: T): Boolean
    fun update(key: String, data: T): Boolean
    fun remove(key: String): Boolean
    fun <T> keySet(prefix: String, decodeFunction: (String) -> T): Set<T>
    fun removeAll(): Boolean

    companion object
}
