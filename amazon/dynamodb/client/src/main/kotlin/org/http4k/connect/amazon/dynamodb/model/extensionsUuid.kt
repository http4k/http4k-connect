package org.http4k.connect.amazon.dynamodb.model

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import java.util.UUID

@JvmName("valueUUID")
fun <VALUE : Value<UUID>> Attribute.Companion.value(vf: ValueFactory<VALUE, UUID>) = Attribute.uuid().value(vf)

@JvmName("valueListUuid")
fun <VALUE : Value<UUID>> Attribute.Companion.list(vf: ValueFactory<VALUE, UUID>) = vf.stringList()
