package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class DeliveryStreamName private constructor(value: String) : ResourceId(value) {
    companion object : StringValueFactory<DeliveryStreamName>(::DeliveryStreamName, 1.minLength)
}
