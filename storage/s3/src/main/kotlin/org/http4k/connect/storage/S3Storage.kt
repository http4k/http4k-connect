package org.http4k.connect.storage

import org.http4k.connect.amazon.s3.S3

/**
 * S3-backed storage implementation. Automatically marshals objects to
 * and from string-value format.
 */
inline fun <reified T : Any> Storage.Companion.S3(s3: S3.Bucket): Storage<T> = object : Storage<T> {
    override fun get(key: String): T? {
        TODO("Not yet implemented")
    }

    override fun set(key: String, data: T) {
        TODO("Not yet implemented")
    }

    override fun create(key: String, data: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(key: String, data: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T): Set<T> {
        TODO("Not yet implemented")
    }

    override fun removeAll(keyPrefix: String): Boolean {
        TODO("Not yet implemented")
    }

}
