package org.http4k.connect.amazon.dynamodb.model

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import java.time.OffsetTime

@JvmName("valueOffsetTime")
fun <VALUE : Value<OffsetTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, OffsetTime>) =
    offsetTime().value(vf)

@JvmName("valueListOffsetTime")
fun <VALUE : Value<OffsetTime>> Attribute.Companion.list(vf: ValueFactory<VALUE, OffsetTime>) = vf.stringList()
