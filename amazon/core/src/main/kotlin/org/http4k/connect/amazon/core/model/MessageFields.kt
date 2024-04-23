package org.http4k.connect.amazon.core.model

import se.ansman.kotshi.JsonSerializable
import com.squareup.moshi.Json

interface MessageFields {
    fun toFields(index: Int): Map<String, String>
    val name: String
    val value: String
    val dataType: DataType
}

fun asList(vararg messageFields: List<MessageFields>) = messageFields.flatMap {
    it.flatMapIndexed { index, messageFields -> messageFields.toFields(index + 1).toList() }
}

@JsonSerializable
data class MessageFieldsDto(
    @Json(name = "DataType") val dataType: DataType,
    @Json(name = "BinaryListValue") val binaryListValues: List<String>? = null,
    @Json(name = "BinaryValue") val binaryValue: String? = null,
    @Json(name = "StringListValue") val stringListValues: List<String>? = null,
    @Json(name = "StringValue") val stringValue: String? = null
)
