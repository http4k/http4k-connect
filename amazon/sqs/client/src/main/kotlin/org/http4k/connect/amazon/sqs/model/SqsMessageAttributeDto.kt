package org.http4k.connect.amazon.sqs.model

import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields
import org.http4k.connect.amazon.core.model.MessageFieldsDto
import org.http4k.connect.model.Base64Blob


internal fun MessageFields.toDto() = when(this) {
    is MessageAttribute -> MessageFieldsDto(
        dataType = dataType,
        binaryValue = if (dataType == DataType.Binary) value else null,
        stringValue = if (dataType != DataType.Binary) value else null
    )
    is MessageSystemAttribute -> MessageFieldsDto(
        dataType = dataType,
        binaryValue = if (dataType == DataType.Binary) value else null,
        stringValue = if (dataType != DataType.Binary) value else null
    )
    else -> error("Unsupported type: ${javaClass.simpleName}")
}

internal fun MessageFieldsDto.toSqs(name: String) = when(dataType) {
    DataType.String, DataType.Number -> if (stringListValues != null) {
        MessageAttribute(name, stringListValues.orEmpty(), dataType)
    } else {
        MessageAttribute(name, stringValue!!, dataType)
    }
    DataType.Binary -> if (binaryListValues != null) {
        MessageAttribute(name, binaryListValues.orEmpty().map(Base64Blob::of))
    } else {
        MessageAttribute(name, Base64Blob.of(binaryValue!!))
    }
}
