package org.http4k.connect.amazon.systemsmanager.model

import dev.forkhandles.values.NonBlankStringValueFactory
import org.http4k.connect.amazon.model.ResourceId

class SSMParameterName private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<SSMParameterName>(::SSMParameterName)
}
