package org.http4k.connect.amazon.core.model

interface MessageFields {
    fun toFields(index: Int): Map<String, String>
    val name: String
    val value: String
    val dataType: DataType
}

fun asList(vararg messageFields: List<MessageFields>) = messageFields.flatMap {
    it.flatMapIndexed { index, messageFields -> messageFields.toFields(index + 1).toList() }
}
