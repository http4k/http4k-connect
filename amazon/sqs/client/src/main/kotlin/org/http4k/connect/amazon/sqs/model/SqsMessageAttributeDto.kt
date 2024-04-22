package org.http4k.connect.amazon.sqs.model

import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SqsMessageAttributeDto(
    val DataType: DataType,
    val BinaryListValues: List<String>? = null,
    val BinaryValue: String? = null,
    val StringListValues: List<String>? = null,
    val StringValue: String? = null
)

internal fun MessageFields.toDto() = when(this) {
    is SQSMessageAttribute.SingularValue -> SqsMessageAttributeDto(
        DataType = dataType,
        StringValue = value
    )
    is SQSMessageAttribute.ListValue -> SqsMessageAttributeDto(
        DataType = dataType,
        StringListValues = values
    )
    else -> error("Unsupported type: ${javaClass.simpleName}")
}
