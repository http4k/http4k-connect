package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class SNSMessageId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<SNSMessageId>(::SNSMessageId)
}

class PhoneNumber private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<PhoneNumber>(::PhoneNumber)
}

class TopicName private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<TopicName>(::TopicName)
}
