package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class SecretId private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<SecretId>(::SecretId)
}

class VersionId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<VersionId>(::VersionId)
}

class VersionStage private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<VersionStage>(::VersionStage)
}
