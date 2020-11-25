package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class SecretId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<SecretId>(::SecretId, 1.minLength) {
        fun of(arn: ARN) = of(arn.value)
    }
}

class KmsKeyId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<KmsKeyId>(::KmsKeyId, 1.minLength)
}

class VersionId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<VersionId>(::VersionId, 1.minLength)
}

class VersionStage private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<VersionStage>(::VersionStage, 1.minLength)
}
