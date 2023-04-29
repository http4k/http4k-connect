package org.http4k.connect.amazon.dynamodb.model

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import java.time.LocalDateTime

@JvmName("valueLocalDateTime")
fun <VALUE : Value<LocalDateTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, LocalDateTime>) =
    Attribute.localDateTime().value(vf)

@JvmName("valueListLocalDateTime")
fun <VALUE : Value<LocalDateTime>> Attribute.Companion.list(vf: ValueFactory<VALUE, LocalDateTime>) = vf.stringList()
