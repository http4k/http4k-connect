package org.http4k.connect.amazon.ec2credentials.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory

class InstanceId private constructor(value: String): StringValue(value) {
    companion object: StringValueFactory<InstanceId>(::InstanceId, { it.startsWith("i-")})
}
