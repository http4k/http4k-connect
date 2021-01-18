package org.http4k.connect.amazon.firehose.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.firehose.FirehoseMoshi
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.DeliveryStreamName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class PutRecord(
    val DeliveryStreamName: DeliveryStreamName,
    val Record: Record
) : FirehoseAction<RecordAdded>(RecordAdded::class, FirehoseMoshi)

@JsonSerializable
data class Record(
    val Data: Base64Blob
)

@JsonSerializable
data class RecordAdded(
    val Encrypted: Boolean,
    val RecordId: String
)
