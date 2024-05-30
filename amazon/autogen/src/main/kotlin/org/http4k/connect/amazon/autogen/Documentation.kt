package org.http4k.connect.amazon.autogen

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory

class Documentation private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<Documentation>(::Documentation)
}
