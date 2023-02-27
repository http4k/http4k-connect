package org.http4k.connect.kafka.rest.model

import com.squareup.moshi.Json
import org.apache.avro.generic.GenericContainer
import org.http4k.connect.model.Base64Blob
import org.http4k.core.ContentType
import org.http4k.core.KAFKA_AVRO_v2
import org.http4k.core.KAFKA_BINARY_v2
import org.http4k.core.KAFKA_JSON_V2
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Records(
    val records: List<Record<*, Any>>,
    @Json(ignore = true) val contentType: ContentType = ContentType.KAFKA_JSON_V2,
    val value_schema: String? = null
) {
    companion object {
        fun Json(records: List<Record<*, *>>) = Records(records, ContentType.KAFKA_JSON_V2)
        fun Avro(records: List<Record<*, GenericContainer>>) = Records(
            records,
            ContentType.KAFKA_AVRO_v2,
            records.first().value.schema.toString()
        )

        fun Binary(records: List<Record<Base64Blob, Base64Blob>>) = Records(records, ContentType.KAFKA_BINARY_v2)
    }
}
