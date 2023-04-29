package org.http4k.connect.amazon.dynamodb.model

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import java.time.ZonedDateTime

@JvmName("valueZonedDateTime")
fun <VALUE : Value<ZonedDateTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, ZonedDateTime>) =
    zonedDateTime().value(vf)

@JvmName("valueListZonedDateTime")
fun <VALUE : Value<ZonedDateTime>> Attribute.Companion.list(vf: ValueFactory<VALUE, ZonedDateTime>) = vf.stringList()
