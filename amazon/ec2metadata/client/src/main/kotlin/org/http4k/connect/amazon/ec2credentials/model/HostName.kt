package org.http4k.connect.amazon.ec2credentials.model

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

class HostName private constructor(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<HostName>(::HostName)
}
