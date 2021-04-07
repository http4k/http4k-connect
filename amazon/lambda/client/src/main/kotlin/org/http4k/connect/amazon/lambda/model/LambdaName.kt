package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory

class FunctionName(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<FunctionName>(::FunctionName)
}
