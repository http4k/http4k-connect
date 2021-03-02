package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

class SecretId private constructor(value: String) : ResourceId(value) {
    companion object : NonEmptyStringValueFactory<SecretId>(::SecretId)
}

class VersionId private constructor(value: String) : StringValue(value) {
    companion object : NonEmptyStringValueFactory<VersionId>(::VersionId)
}

class VersionStage private constructor(value: String) : StringValue(value) {
    companion object : NonEmptyStringValueFactory<VersionStage>(::VersionStage)
}
