package org.http4k.connect.amazon.secretsmanager.model

import dev.forkhandles.values.NonBlankStringValueFactory
import org.http4k.connect.amazon.model.ResourceId

class SecretId private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<SecretId>(::SecretId)
}
