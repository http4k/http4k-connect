package org.http4k.connect.storage

import java.util.concurrent.ConcurrentHashMap

fun <T> Storage.Companion.InMemory() = object : Storage<T> {
    private val byKey = ConcurrentHashMap<String, T>()

    override fun get(key: String): T? = byKey[key]
    override fun set(key: String, data: T) {
        byKey[key] = data
    }

    override fun create(key: String, data: T): Boolean = byKey.putIfAbsent(key, data) == null
    override fun update(key: String, data: T) = byKey[key] != null && byKey.put(key, data) != null

    override fun remove(key: String) = byKey.remove(key) != null

    override fun removeAll(keyPrefix: String): Boolean {
        byKey.keys().iterator().forEach { if (it.startsWith(keyPrefix)) remove(it) }
        return true
    }

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T): Set<T> = byKey.keys.filter { it.startsWith(keyPrefix) }.map { decodeFunction(it) }.toSet()

    override fun toString() = byKey.toString()
}
