package org.http4k.connect.amazon.core.model

import se.ansman.kotshi.JsonSerializable

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
    val DataType: DataType,
    val BinaryListValues: List<String>? = null,
    val BinaryValue: String? = null,
    val StringListValues: List<String>? = null,
    val StringValue: String? = null
)
