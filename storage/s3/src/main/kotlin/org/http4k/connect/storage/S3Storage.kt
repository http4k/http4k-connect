package org.http4k.connect.storage

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.s3.BucketKey
import org.http4k.connect.amazon.s3.S3
import org.http4k.format.AutoMarshalling
import org.http4k.format.Jackson
import java.io.InputStream

/**
 * S3-backed storage implementation. Automatically marshals objects to
 * and from string-value format.
 */
inline fun <reified T : Any> Storage.Companion.S3(s3: S3.Bucket, autoMarshalling: AutoMarshalling = Jackson): Storage<T> = object : Storage<T> {
    override fun get(key: String): T? {
        val value: T? = s3[BucketKey(key)]
            .map<InputStream?, T?, RemoteFailure> {
                it?.reader()?.readText()?.let { autoMarshalling.asA(it) }
            }
            .recover { it.throwIt() }
        return value
    }

    override fun set(key: String, data: T) {
        s3[BucketKey(key)] = autoMarshalling.asInputStream(data)
    }

    override fun create(key: String, data: T) =
        get(key)
            ?.let { false }
            ?: s3.set(BucketKey(key), autoMarshalling.asInputStream(data))
                .map { true }
                .recover { it.throwIt() }

    override fun update(key: String, data: T) =
        s3.set(BucketKey(key), autoMarshalling.asInputStream(data))
            .map { true }
            .recover { it.throwIt() }

    override fun remove(key: String) =
        s3.delete(BucketKey(key))
            .map { true }
            .recover { it.throwIt() }

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T) =
        when (val result = s3.list()) {
            is Success -> result.value
                .filter { it.value.startsWith(keyPrefix) }
                .map { decodeFunction(it.value) }
                .toSet()
            is Failure -> result.reason.throwIt()
        }

    override fun removeAll(keyPrefix: String) = with(keySet(keyPrefix) { BucketKey(it) }) {
        when {
            isEmpty() -> false
            else -> {
                forEach { s3.delete(it) }
                true
            }
        }
    }
}

fun RemoteFailure.throwIt(): Nothing {
    throw Exception(toString())
}

