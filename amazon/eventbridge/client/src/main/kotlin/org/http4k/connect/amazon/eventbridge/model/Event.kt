package org.http4k.connect.amazon.eventbridge.model

import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.model.EventBusName
import org.http4k.connect.amazon.model.EventDetail
import org.http4k.connect.amazon.model.EventDetailType
import org.http4k.connect.amazon.model.EventSource
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Event(
    val Detail: EventDetail,
    val DetailType: EventDetailType,
    val Source: EventSource,
    val EventBusName: EventBusName? = null,
    val Resources: List<ARN>? = null,
    val Time: Timestamp? = null,
    val TraceHeader: String? = null
)
