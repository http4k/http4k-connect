package org.http4k.connect.amazon.cloudwatchlogs.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.cloudwatchlogs.CloudWatchLogsAction
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.model.LogGroupName
import org.http4k.connect.amazon.model.LogStreamName
import se.ansman.kotshi.JsonSerializable


@Http4kConnectAction
@JsonSerializable
data class PutLogEvents(
    val logGroupName: LogGroupName,
    val logStreamName: LogStreamName,
    val logEvents: List<LogEvent>
) : CloudWatchLogsAction<PutLogEventsResponse>(PutLogEventsResponse::class)

@JsonSerializable
data class RejectedLogEventsInfo(
    val expiredLogEventEndIndex: Int?,
    val tooNewLogEventStartIndex: Int?,
    val tooOldLogEventEndIndex: Int?
)

@JsonSerializable
data class PutLogEventsResponse(val rejectedLogEventsInfo: RejectedLogEventsInfo?)

@JsonSerializable
data class LogEvent(val message: String, val timestamp: Timestamp)

