package org.http4k.connect.amazon.model

interface MessageFields {
    fun toFields(index: Int): Map<String, String>
}

interface MessageAttribute : MessageFields
interface MessageSystemAttribute : MessageFields

enum class DataType {
    String, Number, Binary
}

fun asList(vararg messageFields: List<MessageFields>) = messageFields.flatMap {
    it.flatMapIndexed { index, messageFields -> messageFields.toFields(index + 1).toList() }
}
