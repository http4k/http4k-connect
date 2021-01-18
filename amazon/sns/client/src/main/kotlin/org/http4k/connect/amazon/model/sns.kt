package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class SNSMessageId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<SNSMessageId>(::SNSMessageId, 1.minLength)
}

class PhoneNumber private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<PhoneNumber>(::PhoneNumber, 1.minLength)
}
