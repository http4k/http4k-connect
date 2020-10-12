package org.http4k.connect.storage

import org.http4k.format.AutoMarshalling
import org.http4k.format.Jackson
import java.io.File

/**
 * Simple On-Disk, file-backed storage implementation.
 */
inline fun <reified T : Any> Storage.Companion.FileBased(dir: File, autoMarshalling: AutoMarshalling = Jackson) = object : Storage<T> {
    override fun get(key: String): T? = File(dir, key).takeIf { it.exists() }?.readText()?.let { autoMarshalling.asA<T>(it) }
    override fun set(key: String, data: T) {
        File(dir, key).writeText(autoMarshalling.asFormatString(data))
    }

    override fun create(key: String, data: T) = File(dir, key).takeUnless { it.exists() }?.also { set(key, data) } != null

    override fun update(key: String, data: T) = File(dir, key).takeIf { it.exists() }?.also { set(key, data) } != null

    override fun remove(key: String) = File(dir, key).delete()

    override fun removeAll(keyPrefix: String): Boolean {
        val files = listKeysWith(keyPrefix)
        return when {
            files.isEmpty() -> false
            else -> {
                files.forEach { it.delete() }
                true
            }
        }
    }

    private fun listKeysWith(keyPrefix: String): List<File> =
        (dir.listFiles { pathname -> pathname.isFile && pathname.name.startsWith(keyPrefix) }
            ?: emptyArray()).toList()

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T): Set<T> = listKeysWith(keyPrefix).map { decodeFunction(it.name) }.toSet()

    override fun toString() = listKeysWith("").joinToString(",") { it.name }
}
