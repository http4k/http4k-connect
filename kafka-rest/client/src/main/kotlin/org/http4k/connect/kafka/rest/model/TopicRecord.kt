package org.http4k.connect.kafka.rest.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class TopicRecord(
    val topic: Topic,
    val key: Any?,
    val `value`: Any,
    val partition: PartitionId,
    val offset: Offset
)
