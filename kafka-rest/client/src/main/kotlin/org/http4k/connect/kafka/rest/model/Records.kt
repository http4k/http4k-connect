package org.http4k.connect.kafka.rest.model

import org.http4k.core.ContentType
import org.http4k.core.KAFKA_AVRO_v2
import org.http4k.core.KAFKA_BINARY_v2
import org.http4k.core.KAFKA_JSON_V2
import se.ansman.kotshi.JsonSerializable

sealed interface Records {
    fun contentType(): ContentType

    @JsonSerializable
    data class Json(val records: List<JsonRecord<*, *>>) : Records {
        override fun contentType() = ContentType.KAFKA_JSON_V2
    }

    @JsonSerializable
    data class Avro(
        val value_schema: String,
        val records: List<AvroRecord<*, *>>
    ) : Records {
        override fun contentType() = ContentType.KAFKA_AVRO_v2
    }

    @JsonSerializable
    data class Binary(val records: List<BinaryRecord>) : Records {
        override fun contentType() = ContentType.KAFKA_BINARY_v2
    }
}
