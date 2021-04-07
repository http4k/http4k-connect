package org.http4k.connect.amazon.s3.model

import dev.forkhandles.values.NonBlankStringValueFactory
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.ResourceId

class BucketName private constructor(value: String) : ResourceId(value) {

    fun toUri(region: Region) = AwsService.of("$this.s3").toUri(region)

    companion object : NonBlankStringValueFactory<BucketName>(::BucketName)
}
