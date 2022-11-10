package org.http4k.connect.google.analytics.ua.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class ClientId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ClientId>(::ClientId)
}
