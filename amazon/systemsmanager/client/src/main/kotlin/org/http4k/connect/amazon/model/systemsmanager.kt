package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory

enum class ParameterType {
    String, StringList, SecureString
}

class SSMParameterName private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<SSMParameterName>(::SSMParameterName)
}
