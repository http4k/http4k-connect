package org.http4k.connect.kafka.rest.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class ConsumerName private constructor(override val value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ConsumerName>(::ConsumerName)
}
