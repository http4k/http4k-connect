package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class BucketName private constructor(value: String) : ResourceId(value) {

    fun toUri(region: Region) = AwsService.of("$this.s3").toUri(region)

    companion object : StringValueFactory<BucketName>(::BucketName, 1.minLength)
}

class BucketKey private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<BucketKey>(::BucketKey, 1.minLength)
}
