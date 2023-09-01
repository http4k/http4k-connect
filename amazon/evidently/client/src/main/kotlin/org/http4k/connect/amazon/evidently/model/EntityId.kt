package org.http4k.connect.amazon.evidently.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.length

class EntityId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<EntityId>(::EntityId, (1..512).length)
}
