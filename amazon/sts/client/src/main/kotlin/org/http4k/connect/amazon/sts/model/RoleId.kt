package org.http4k.connect.amazon.sts.model

import dev.forkhandles.values.NonBlankStringValueFactory
import org.http4k.connect.amazon.model.ResourceId

class RoleId private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<RoleId>(::RoleId)
}
