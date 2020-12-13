package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class QueueName private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<QueueName>(::QueueName, 1.minLength)
}
