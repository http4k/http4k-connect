package org.http4k.connect.amazon.cloudwatchlogs

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.JsonError
import org.http4k.connect.amazon.cloudwatchlogs.action.CreateLogGroup
import org.http4k.connect.amazon.cloudwatchlogs.action.CreateLogStream
import org.http4k.connect.amazon.cloudwatchlogs.action.DeleteLogGroup
import org.http4k.connect.amazon.cloudwatchlogs.action.DeleteLogStream
import org.http4k.connect.amazon.cloudwatchlogs.action.FilterLogEvents
import org.http4k.connect.amazon.cloudwatchlogs.action.FilteredLogEvent
import org.http4k.connect.amazon.cloudwatchlogs.action.FilteredLogEvents
import org.http4k.connect.amazon.cloudwatchlogs.action.PutLogEvents
import org.http4k.connect.amazon.cloudwatchlogs.action.SearchedLogStreams
import org.http4k.connect.amazon.model.LogGroupName
import org.http4k.connect.amazon.model.NextToken
import org.http4k.connect.storage.Storage
import java.util.UUID


fun AmazonJsonFake.createLogGroup(logGroups: Storage<LogGroup>) = route<CreateLogGroup> {
    when (logGroups[it.logGroupName.value]) {
        null -> logGroups[it.logGroupName.value] = LogGroup(mutableMapOf())

        else -> JsonError("conflict", "${it.logGroupName} already exists")
    }
}

fun AmazonJsonFake.createLogStream(logGroups: Storage<LogGroup>) = route<CreateLogStream> {
    when (val existing: LogGroup? = logGroups[it.logGroupName.value]) {
        null -> JsonError("not found", "${it.logGroupName} not found")
        else -> {
            existing.streams[it.logStreamName] = mutableListOf()
        }
    }
}

fun AmazonJsonFake.deletaLogStream(logGroups: Storage<LogGroup>) = route<DeleteLogStream> {
    when (val group = logGroups[it.logGroupName.value]) {
        null -> JsonError("not found", "${it.logGroupName} not found")
        else -> group.streams -= it.logStreamName
    }
}

fun AmazonJsonFake.deletaLogGroup(logGroups: Storage<LogGroup>) = route<DeleteLogGroup> {
    when (logGroups[it.logGroupName.value]) {
        null -> JsonError("not found", "${it.logGroupName} not found")
        else -> logGroups -= it.logGroupName.value
    }
}

fun AmazonJsonFake.putLogEvents(logGroups: Storage<LogGroup>) = route<PutLogEvents> { req ->
    when (val group = logGroups[req.logGroupName.value]) {
        null -> JsonError("not found", "${req.logGroupName} not found")
        else -> group.streams.getOrPut(req.logStreamName) { mutableListOf() } += req.logEvents.map {
            FilteredLogEvent(UUID(0, 0).toString(), it.timestamp, req.logStreamName, it.message, it.timestamp)
        }
    }
}

fun AmazonJsonFake.filterLogEvents(logGroups: Storage<LogGroup>) = route<FilterLogEvents> {
    val group = (it.logGroupName ?: it.logGroupIdentifier?.resourceId(LogGroupName::of))
        ?.let { logGroups[it.value] }

    when (group) {
        null -> FilteredLogEvents(emptyList(), NextToken.of("123"), emptyList())
        else -> FilteredLogEvents(
            group.streams.flatMap { it.value }.sortedBy { it.timestamp.value },
            NextToken.of("123"), group.streams.map {
                SearchedLogStreams(it.key, true)
            }
        )
    }
}
