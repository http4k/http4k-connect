package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.sqs.action.DataType.Binary

interface MessageFields {
    fun toFields(index: Int): Map<String, String>
}

interface MessageAttribute : MessageFields
interface MessageSystemAttribute : MessageFields

sealed class SQSMessageAttribute(private val name: String, private val category: String, private val dataType: DataType?) : MessageFields {
    override fun toFields(index: Int): Map<String, String> =
        (listOfNotNull(
            "$category.$index.Name" to name,
            dataType?.let { "$category.$index.Value.DataType" to it.name }
        ).toMap() + toCustomFields(index))

    protected abstract fun toCustomFields(index: Int): Map<String, String>

    internal class SingularValue(
        name: String,
        private val category: String,
        private val typePrefix: String,
        private val value: String,
        dataType: DataType?
    ) : SQSMessageAttribute(name, category, dataType) {
        override fun toCustomFields(index: Int) =
            mapOf("$category.$index.Value.${typePrefix}Value" to value)
    }

//    internal class ListValue(
//        private val values: List<String>,
//        private val category: String,
//        private val typePrefix: String,
//        name: String,
//        dataType: DataType?
//    ) : SQSMessageAttribute(name, category, dataType) {
//        override fun toCustomFields(index: Int) =
//            values.mapIndexed { i, it ->
//                "$category.$index.Value.${typePrefix}Value.${i + 1}" to it
//            }.toMap()
//    }
}

fun MessageAttribute(name: String, value: String, dataType: DataType? = null): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(name, "MessageAttribute", "String", value, dataType) {}

//@JvmName("MessageAttributeStringList")
//fun MessageAttribute(name: String, value: List<String>, dataType: DataType? = null): MessageAttribute =
//    object : MessageAttribute,
//        MessageFields by SQSMessageAttribute.ListValue(value, "MessageAttribute", "StringList", name, dataType) {}

fun MessageAttribute(name: String, value: Base64Blob): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(name, "MessageAttribute", "Binary", value.value, Binary) {}

//@JvmName("MessageAttributeBinaryList")
//fun MessageAttribute(name: String, value: List<Base64Blob>): MessageAttribute =
//    object : MessageAttribute,
//        MessageFields by SQSMessageAttribute.ListValue(
//            value.map { it.value },
//            "MessageAttribute",
//            "StringList",
//            name,
//            Binary
//        ) {}

fun MessageSystemAttribute(name: String, value: String, dataType: DataType? = null): MessageSystemAttribute =
    object : MessageSystemAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(name, "MessageSystemAttribute", "String", value, dataType) {}

//@JvmName("MessageSystemAttributeStringList")
//fun MessageSystemAttribute(name: String, value: List<String>, dataType: DataType? = null): MessageSystemAttribute =
//    object : MessageSystemAttribute,
//        MessageFields by SQSMessageAttribute.ListValue(value, "MessageSystemAttribute", "StringList", name, dataType) {}

fun MessageSystemAttribute(name: String, value: Base64Blob): MessageSystemAttribute =
    object : MessageSystemAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(
            name,
            "MessageSystemAttribute",
            "Binary",
            value.value,
            Binary
        ) {}

//@JvmName("MessageSystemAttributeBinaryList")
//fun MessageSystemAttribute(name: String, value: List<Base64Blob>): MessageSystemAttribute =
//    object : MessageSystemAttribute,
//        MessageFields by SQSMessageAttribute.ListValue(
//            value.map { it.value },
//            "MessageSystemAttribute",
//            "StringList",
//            name,
//            dataType
//        ) {}

enum class DataType {
    String, Number, Binary
}
