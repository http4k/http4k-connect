package org.http4k.connect.amazon.core.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.regex

class Region private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<Region>(::Region, "[a-z]+-[a-z]+-\\d".regex)
}
