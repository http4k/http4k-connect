package org.http4k.connect.amazon.sqs.model

import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields

interface MessageAttribute : MessageFields

fun MessageAttribute(name: String, value: String, dataType: DataType): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(name, "MessageAttribute", "String", value, dataType) {}

@JvmName("MessageAttributeStringList")
fun MessageAttribute(name: String, value: List<String>, dataType: DataType): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SQSMessageAttribute.ListValue(value, "MessageAttribute", "StringList", name, dataType) {}

fun MessageAttribute(name: String, value: Base64Blob): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(
            name, "MessageAttribute", "Binary", value.value,
            DataType.Binary
        ) {}

@JvmName("MessageAttributeBinaryList")
fun MessageAttribute(name: String, value: List<Base64Blob>): MessageAttribute =
    object : MessageAttribute,
        MessageFields by SQSMessageAttribute.ListValue(
            value.map { it.value },
            "MessageAttribute",
            "BinaryList",
            name,
            DataType.Binary
        ) {}
