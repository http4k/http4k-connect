package org.http4k.connect.amazon.sqs.model

import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields

interface MessageSystemAttribute : MessageFields

fun MessageSystemAttribute(name: String, value: String, dataType: DataType): MessageSystemAttribute =
    object : MessageSystemAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(name, "MessageSystemAttribute", "String", value, dataType) {}

@JvmName("MessageSystemAttributeStringList")
fun MessageSystemAttribute(name: String, value: List<String>, dataType: DataType): MessageSystemAttribute =
    object : MessageSystemAttribute,
        MessageFields by SQSMessageAttribute.ListValue(value, "MessageSystemAttribute", "StringList", name, dataType) {}

fun MessageSystemAttribute(name: String, value: Base64Blob): MessageSystemAttribute =
    object : MessageSystemAttribute,
        MessageFields by SQSMessageAttribute.SingularValue(
            name,
            "MessageSystemAttribute",
            "Binary",
            value.value,
            DataType.Binary
        ) {}

@JvmName("MessageSystemAttributeBinaryList")
fun MessageSystemAttribute(name: String, value: List<Base64Blob>): MessageSystemAttribute =
    object : MessageSystemAttribute,
        MessageFields by SQSMessageAttribute.ListValue(
            value.map { it.value },
            "MessageSystemAttribute",
            "BinaryList",
            name,
            DataType.Binary
        ) {}
