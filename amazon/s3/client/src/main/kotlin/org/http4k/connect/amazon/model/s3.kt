package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

class BucketName private constructor(value: String) : ResourceId(value) {

    fun toUri(region: Region) = AwsService.of("$this.s3").toUri(region)

    companion object : NonEmptyStringValueFactory<BucketName>(::BucketName)
}

class BucketKey private constructor(value: String) : StringValue(value) {
    companion object : NonEmptyStringValueFactory<BucketKey>(::BucketKey)
}
