package org.http4k.connect

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

abstract class Http4kConnectMoshiAdapter<T> : JsonAdapter<T>() {
    @Suppress("UNCHECKED_CAST")
    override fun fromJson(reader: JsonReader): T = fromJsonFields(reader.readJsonValue() as Map<String, Any>)

    protected abstract fun fromJsonFields(fields: Map<String, Any>): T

    protected abstract fun fromObject(writer: JsonWriter, it: T)

    override fun toJson(writer: JsonWriter, value: T?) {
        value?.let {
            with(writer) {
                beginObject()
                fromObject(writer, it)
                endObject()
            }
        }
    }
}
