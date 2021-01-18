package org.http4k.connect.amazon.firehose.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.firehose.FirehoseMoshi
import org.http4k.connect.amazon.model.DeliveryStreamName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class PutRecordBatch(
    val DeliveryStreamName: DeliveryStreamName,
    val Records: List<Record>
) : FirehoseAction<BatchResult>(BatchResult::class, FirehoseMoshi)

@JsonSerializable
data class RequestResponses(
    val ErrorCode: String?,
    val ErrorMessage: String?,
    val RecordId: String
)

@JsonSerializable
data class BatchResult(
    val Encrypted: Boolean,
    val FailedPutCount: Int,
    val RequestResponses: List<RequestResponses>?
)
