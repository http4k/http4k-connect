package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class FunctionName(value: String) : ResourceId(value) {
    companion object : StringValueFactory<FunctionName>(::FunctionName, 1.minLength)
}
