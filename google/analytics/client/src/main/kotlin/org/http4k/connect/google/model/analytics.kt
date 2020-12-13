package org.http4k.connect.google.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class ClientId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<ClientId>(::ClientId, 1.minLength)
}

class TrackingId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TrackingId>(::TrackingId, 1.minLength)
}
