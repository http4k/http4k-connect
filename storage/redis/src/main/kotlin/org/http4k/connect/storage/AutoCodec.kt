package org.http4k.connect.storage

import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.codec.StringCodec
import org.http4k.format.AutoMarshalling
import java.nio.ByteBuffer

inline fun <reified T : Any> AutoCodec(autoMarshalling: AutoMarshalling) = object : RedisCodec<String, T> {
    override fun decodeKey(bytes: ByteBuffer): String = String(bytes.array())

    override fun decodeValue(bytes: ByteBuffer) = autoMarshalling.asA<T>(String(bytes.array()))

    override fun encodeKey(key: String): ByteBuffer = StringCodec().encodeKey(key)

    override fun encodeValue(value: T): ByteBuffer = ByteBuffer.wrap(autoMarshalling.asFormatString(value).toByteArray())
}


