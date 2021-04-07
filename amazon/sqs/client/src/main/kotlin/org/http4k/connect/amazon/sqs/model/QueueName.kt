package org.http4k.connect.amazon.sqs.model

import dev.forkhandles.values.NonBlankStringValueFactory
import org.http4k.connect.amazon.model.ResourceId

class QueueName private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<QueueName>(::QueueName)
}
