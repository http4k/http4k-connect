package org.http4k.connect.google.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class ClientId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ClientId>(::ClientId)
}

class TrackingId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<TrackingId>(::TrackingId)
}
