package org.http4k.connect.kafka.rest

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import org.http4k.connect.kafka.rest.model.Record
import org.http4k.connect.kafka.rest.model.Records

/**
LocalDate -> Int
LocalDate.ofEpochDay(daysFromEpoch.toLong())
val epochDays = date.toEpochDay()
return epochDays.toInt()

LocalTime -> Int
LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.toNanos(millisFromMidnight.toLong()))
TimeUnit.NANOSECONDS.toMillis(time.toNanoOfDay()).toInt()

instant -> long
Instant.ofEpochMilli(millisFromEpoch)

LocalDateTime -> long
val instant = timestampMillisConversion.fromLong(millisFromEpoch, schema, type)
return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
}

override fun toLong(timestamp: LocalDateTime, schema: Schema, type: LogicalType): Long {
val instant = timestamp.toInstant(ZoneOffset.UTC)
return timestampMillisConversion.toLong(instant, schema, type)
}

LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.toNanos(millisFromMidnight.toLong()))

override fun toInt(time: LocalTime, schema: Schema, type: LogicalType): Int =
TimeUnit.NANOSECONDS.toMillis(time.toNanoOfDay()).toInt()
 */


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

