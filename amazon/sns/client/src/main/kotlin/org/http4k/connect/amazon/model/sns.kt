package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

class SNSMessageId private constructor(value: String) : StringValue(value) {
    companion object : NonEmptyStringValueFactory<SNSMessageId>(::SNSMessageId)
}

class PhoneNumber private constructor(value: String) : StringValue(value) {
    companion object : NonEmptyStringValueFactory<PhoneNumber>(::PhoneNumber)
}

class TopicName private constructor(value: String) : ResourceId(value) {
    companion object : NonEmptyStringValueFactory<TopicName>(::TopicName)
}
