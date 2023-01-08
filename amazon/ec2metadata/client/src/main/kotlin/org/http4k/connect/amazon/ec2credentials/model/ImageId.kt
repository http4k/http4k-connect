package org.http4k.connect.amazon.ec2credentials.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory

class ImageId private constructor(value: String): StringValue(value) {
    companion object: StringValueFactory<ImageId>(::ImageId, { it.startsWith("ami-")})
}
