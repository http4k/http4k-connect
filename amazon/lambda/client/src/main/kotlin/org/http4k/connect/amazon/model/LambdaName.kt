package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class LambdaName(value: String) : StringValue(value) {
    companion object : StringValueFactory<LambdaName>(::LambdaName, 1.minLength)
}
