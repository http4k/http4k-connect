package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory

class BucketName private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<BucketName>(::BucketName, String::isNotEmpty)
}

class BucketKey private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<BucketKey>(::BucketKey, String::isNotEmpty)
}
