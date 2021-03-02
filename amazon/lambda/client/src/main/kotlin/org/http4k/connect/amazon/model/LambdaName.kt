package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonEmptyStringValueFactory

class FunctionName(value: String) : ResourceId(value) {
    companion object : NonEmptyStringValueFactory<FunctionName>(::FunctionName)
}
