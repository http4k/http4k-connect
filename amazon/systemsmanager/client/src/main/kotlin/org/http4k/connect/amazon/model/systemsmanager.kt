package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

enum class ParameterType {
    String, StringList, SecureString
}

class SSMParameterName private constructor(value: String) : ResourceId(value) {
    companion object : StringValueFactory<SSMParameterName>(::SSMParameterName, 1.minLength)
}
