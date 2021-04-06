package org.http4k.connect.amazon.events

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Item
import org.http4k.connect.amazon.model.Region
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Dynamodb(
    val Keys: Item? = null,
    val NewImage: Item? = null,
    val OldImage: Item? = null,
    val SequenceNumber: String? = null,
    val SizeBytes: Long? = null,
    val StreamViewType: StreamViewType? = null
)

enum class EventName {
    INSERT, MODIFY, REMOVE
}

enum class StreamViewType {
    NEW_IMAGE, OLD_IMAGE, NEW_AND_OLD_IMAGES, KEYS_ONLY
}

@JsonSerializable
data class StreamRecord(
    val eventID: String? = null,
    val eventName: EventName? = null,
    val eventVersion: String? = null,
    val eventSource: String? = null,
    val awsRegion: Region? = null,
    val dynamodb: Dynamodb? = null,
    val eventSourceARN: ARN? = null
)

@JsonSerializable
data class DynamoDbEvent(
    val Records: List<StreamRecord>? = null
)
