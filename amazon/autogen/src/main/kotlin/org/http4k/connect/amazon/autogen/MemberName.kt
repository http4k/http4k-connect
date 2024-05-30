package org.http4k.connect.amazon.autogen

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class MemberName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<MemberName>(::MemberName)
}
