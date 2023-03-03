package org.http4k.connect.kafka.rest

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import org.http4k.connect.kafka.rest.model.Record
import org.http4k.connect.kafka.rest.model.Records

class RecordsJsonAdapter(moshi: Moshi) : JsonAdapter<Records>() {
    private val recordsAdapter: JsonAdapter<List<Record<*, Any>>> = moshi.adapter(
        newParameterizedType(
            List::class.javaObjectType,
            newParameterizedType(
                Record::class.javaObjectType, Any::class.javaObjectType,
                Any::class.javaObjectType
            )
        ),
        setOf(),
        "records"
    )

    override fun toJson(writer: JsonWriter, `value`: Records?) {
        if (`value` == null) {
            writer.nullValue()
            return
        }
        writer
            .beginObject()
            .name("records").apply { recordsAdapter.toJson(this, `value`.records) }
            .name("key_schema").value(`value`.key_schema)
            .name("value_schema").value(`value`.value_schema)
            .endObject()
    }

    override fun fromJson(reader: JsonReader): Records? {
        TODO("Not yet implemented")
    }
}
