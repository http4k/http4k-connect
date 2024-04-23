package org.http4k.connect.amazon.sqs.model

import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields
import org.http4k.connect.model.Base64Blob
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
    is MessageAttribute -> SqsMessageAttributeDto(
        DataType = dataType,
        BinaryValue = if (dataType == DataType.Binary) value else null,
        StringValue = if (dataType != DataType.Binary) value else null
    )
    is MessageSystemAttribute -> SqsMessageAttributeDto(
        DataType = dataType,
        BinaryValue = if (dataType == DataType.Binary) value else null,
        StringValue = if (dataType != DataType.Binary) value else null
    )
    else -> error("Unsupported type: ${javaClass.simpleName}")
}

// TODO fixme
internal fun SqsMessageAttributeDto.toInternal(name: String) = when(DataType) {
    org.http4k.connect.amazon.core.model.DataType.String -> MessageAttribute(name, StringValue!!, DataType)
    org.http4k.connect.amazon.core.model.DataType.Binary -> MessageAttribute(name, Base64Blob.of(BinaryValue!!))
    org.http4k.connect.amazon.core.model.DataType.Number -> MessageAttribute(name, StringValue!!, DataType)
}
