package org.http4k.connect.amazon.model

import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import org.http4k.connect.amazon.core.model.ResourceId

class LogGroupName private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<LogGroupName>(::LogGroupName)
}

class LogStreamName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<LogStreamName>(::LogStreamName)
}

class LogIndex private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<LogIndex>(::LogIndex)
}

class NextToken private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<NextToken>(::NextToken)
}

class EventId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<EventId>(::EventId)
}
