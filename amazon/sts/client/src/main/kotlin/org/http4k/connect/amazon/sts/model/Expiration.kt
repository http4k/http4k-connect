package org.http4k.connect.amazon.sts.model

import dev.forkhandles.values.ZonedDateTimeValue
import dev.forkhandles.values.ZonedDateTimeValueFactory
import java.time.ZonedDateTime

class Expiration private constructor(value: ZonedDateTime) : ZonedDateTimeValue(value) {
    companion object : ZonedDateTimeValueFactory<Expiration>(::Expiration)
}
