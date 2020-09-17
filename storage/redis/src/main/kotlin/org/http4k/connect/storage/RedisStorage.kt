package org.http4k.connect.storage

import io.lettuce.core.RedisClient.create
import io.lettuce.core.RedisURI
import io.lettuce.core.SetArgs.Builder
import io.lettuce.core.api.sync.RedisCommands
import org.http4k.core.Uri
import org.http4k.format.AutoMarshalling
import java.net.URI
import java.util.concurrent.TimeUnit.HOURS

fun <T> Storage.Companion.Redis(redis: RedisCommands<String, T>) = object : Storage<T> {
    private val lifetime = HOURS.toSeconds(1)

    override fun get(key: String): T? = redis.get(key)

    override fun set(key: String, data: T) {
        redis.set(key, data, Builder.ex(lifetime))
    }

    override fun create(key: String, data: T): Boolean =
        redis.set(key, data, Builder.nx().ex(lifetime)) == "OK"

    override fun update(key: String, data: T) = when {
        redis.get(key) == null -> false
        else -> redis.set(key, data, Builder.xx().ex(lifetime)) == "OK"
    }

    override fun remove(key: String): Boolean = redis.del(key) == 1L

    override fun removeAll(): Boolean {
        redis.flushall()
        return true
    }

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T): Set<T> =
        redis.keys("$keyPrefix*").map { decodeFunction(it) }.toSet()
}

inline fun <reified T : Any> Storage.Companion.Redis(uri: Uri, autoMarshalling: AutoMarshalling) =
    Redis(create(RedisURI.create(URI(uri.toString()))).connect(AutoCodec<T>(autoMarshalling)).sync())
