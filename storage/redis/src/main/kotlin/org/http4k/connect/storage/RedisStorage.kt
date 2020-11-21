package org.http4k.connect.storage

import io.lettuce.core.RedisClient.create
import io.lettuce.core.RedisURI
import io.lettuce.core.SetArgs.Builder
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.codec.RedisCodec
import org.http4k.core.Uri
import org.http4k.format.AutoMarshalling
import org.http4k.format.Jackson
import java.net.URI
import java.util.concurrent.TimeUnit.HOURS

/**
 * Connect to Redis using Automarshalling
 */
inline fun <reified T : Any> Storage.Companion.Redis(uri: Uri, autoMarshalling: AutoMarshalling = Jackson) =
    Redis(uri, AutoRedisCodec<T>(autoMarshalling))

/**
 * Connect to Redis using custom codec
 */
fun <T : Any> Storage.Companion.Redis(uri: Uri, codec: RedisCodec<String, T>) =
    Redis(create(uri.asRedis()).connect(codec).sync())


/**
 * Redis-backed storage implementation. You probably want to use one of the builder functions instead of this
 */
fun <T : Any> Storage.Companion.Redis(redis: RedisCommands<String, T>) = object : Storage<T> {

    private val lifetime = HOURS.toSeconds(1)

    override fun get(key: String): T? = redis.get(key)

    override fun set(key: String, data: T) {
        val result = redis.set(key, data, Builder.ex(lifetime))
        if (result != "OK") throw RuntimeException(result)
    }

    override fun remove(key: String): Boolean = redis.del(key) == 1L

    override fun removeAll(keyPrefix: String): Boolean {
        val keys = redis.keys("$keyPrefix*")
        return if (keys.isEmpty()) false
        else {
            redis.del(*keys.toTypedArray())
            true
        }
    }

    override fun keySet(keyPrefix: String) =
        redis.keys("$keyPrefix*").toSet()
}

fun Uri.asRedis() = RedisURI.create(URI(toString()))
