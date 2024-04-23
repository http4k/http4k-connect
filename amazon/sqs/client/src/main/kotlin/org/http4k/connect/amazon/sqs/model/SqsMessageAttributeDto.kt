package org.http4k.connect.amazon.sqs.model

import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields
import org.http4k.connect.amazon.core.model.MessageFieldsDto
import org.http4k.connect.model.Base64Blob

internal fun MessageFields.toDto() = when(this) {
    is MessageAttribute -> MessageFieldsDto(
        DataType = dataType,
        BinaryValue = if (dataType == DataType.Binary) value else null,
        StringValue = if (dataType != DataType.Binary) value else null
    )
    is MessageSystemAttribute -> MessageFieldsDto(
        DataType = dataType,
        BinaryValue = if (dataType == DataType.Binary) value else null,
        StringValue = if (dataType != DataType.Binary) value else null
    )
    else -> error("Unsupported type: ${javaClass.simpleName}")
}

// TODO fixme
internal fun MessageFieldsDto.toSqs(name: String) = when(DataType) {
    org.http4k.connect.amazon.core.model.DataType.String -> MessageAttribute(name, StringValue!!, DataType)
    org.http4k.connect.amazon.core.model.DataType.Binary -> MessageAttribute(name, Base64Blob.of(BinaryValue!!))
    org.http4k.connect.amazon.core.model.DataType.Number -> MessageAttribute(name, StringValue!!, DataType)
}
