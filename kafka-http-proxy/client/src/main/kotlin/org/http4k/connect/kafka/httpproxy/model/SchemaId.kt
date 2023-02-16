package org.http4k.connect.kafka.httpproxy.model

import dev.forkhandles.values.LongValue
import dev.forkhandles.values.LongValueFactory

class SchemaId private constructor(override val value: Long) : LongValue(value) {
    companion object : LongValueFactory<SchemaId>(::SchemaId)
}
