package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonEmptyStringValueFactory

enum class ParameterType {
    String, StringList, SecureString
}

class SSMParameterName private constructor(value: String) : ResourceId(value) {
    companion object : NonEmptyStringValueFactory<SSMParameterName>(::SSMParameterName)
}
