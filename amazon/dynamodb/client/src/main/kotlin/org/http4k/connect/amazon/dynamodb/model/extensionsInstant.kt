package org.http4k.connect.amazon.dynamodb.model

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import java.time.Instant

@JvmName("valueInstant")
fun <VALUE : Value<Instant>> Attribute.Companion.value(vf: ValueFactory<VALUE, Instant>) = instant().value(vf)

@JvmName("valueListInstant")
fun <VALUE : Value<Instant>> Attribute.Companion.list(vf: ValueFactory<VALUE, Instant>) = vf.stringList()

@JvmName("valueSetInstant")
fun <VALUE : Value<Instant>> Attribute.Companion.set(vf: ValueFactory<VALUE, Instant>) = vf.stringList()
