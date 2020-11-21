package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonEmptyStringValue

class SecretId(value: String) : NonEmptyStringValue(value) {
    constructor(arn: ARN) : this(arn.value)
}

class KmsKeyId(value: String) : NonEmptyStringValue(value) {
    constructor(arn: ARN) : this(arn.value)
}

class VersionId(value: String) : NonEmptyStringValue(value)

class VersionStage(value: String) : NonEmptyStringValue(value)
