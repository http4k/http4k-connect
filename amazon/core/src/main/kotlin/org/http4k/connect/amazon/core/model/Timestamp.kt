package org.http4k.connect.amazon.core.model

import dev.forkhandles.values.LongValue
import dev.forkhandles.values.LongValueFactory
import dev.forkhandles.values.minValue
import java.time.Instant

class Timestamp private constructor(value: Long) : LongValue(value) {
    fun toInstant(): Instant = Instant.ofEpochSecond(value)

    companion object : LongValueFactory<Timestamp>(::Timestamp, 0L.minValue) {
        fun of(value: Instant) = of(value.epochSecond)
    }
}
