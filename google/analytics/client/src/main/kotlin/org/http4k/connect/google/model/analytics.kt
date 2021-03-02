package org.http4k.connect.google.model

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

class ClientId private constructor(value: String) : StringValue(value) {
    companion object : NonEmptyStringValueFactory<ClientId>(::ClientId)
}

class TrackingId private constructor(value: String) : StringValue(value) {
    companion object : NonEmptyStringValueFactory<TrackingId>(::TrackingId)
}
