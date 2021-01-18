package org.http4k.connect.amazon.sns.action

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.DataType
import org.http4k.connect.amazon.model.DataType.Binary
import org.http4k.connect.amazon.model.MessageAttribute
import org.http4k.connect.amazon.model.MessageFields

sealed class SNSMessageAttribute(
    private val name: String,
    private val category: String,
    private val dataType: DataType?
) : MessageFields {
    override fun toFields(index: Int): Map<String, String> =
        (listOfNotNull(
            "$category.entry.$index.Name" to name,
            dataType?.let { "$category.entry.$index.Value.DataType" to it.name }
        ).toMap() + toCustomFields(index))

    protected abstract fun toCustomFields(index: Int): Map<String, String>

    internal class SingularValue(
        name: String,
        private val category: String,
        private val typePrefix: String,
        private val value: String,
        dataType: DataType?
    ) : SNSMessageAttribute(name, category, dataType) {
        override fun toCustomFields(index: Int) =
            mapOf("$category.entry.$index.Value.${typePrefix}Value" to value)
    }

    internal class ListValue(
        private val values: List<String>,
        private val category: String,
        private val typePrefix: String,
        name: String,
        dataType: DataType?
    ) : SNSMessageAttribute(name, category, dataType) {
        override fun toCustomFields(index: Int) =
            values.mapIndexed { i, it ->
                "$category.entry.$index.Value.${typePrefix}Value.${i + 1}" to it
            }.toMap()
    }
}

fun MessageAttribute(name: String, value: String, dataType: DataType): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SNSMessageAttribute.SingularValue(name, "MessageAttributes", "String", value, dataType) {}

@JvmName("MessageAttributeStringList")
fun MessageAttribute(name: String, value: List<String>, dataType: DataType): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SNSMessageAttribute.ListValue(value, "MessageAttributes", "StringList", name, dataType) {}

fun MessageAttribute(name: String, value: Base64Blob): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SNSMessageAttribute.SingularValue(
            name, "MessageAttributes", "Binary", value.value,
            Binary
        ) {}

@JvmName("MessageAttributeBinaryList")
fun MessageAttribute(name: String, value: List<Base64Blob>): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SNSMessageAttribute.ListValue(
            value.map { it.value },
            "MessageAttributes",
            "StringList",
            name,
            Binary
        ) {}

