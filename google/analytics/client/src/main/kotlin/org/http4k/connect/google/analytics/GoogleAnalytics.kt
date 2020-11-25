package org.http4k.connect.google.analytics

import dev.forkhandles.result4k.Result
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength
import org.http4k.connect.RemoteFailure

interface GoogleAnalytics {
    fun pageView(
        userAgent: String,
        clientId: ClientId,
        documentTitle: String,
        documentPath: String,
        documentHost: String
    ): Result<Unit, RemoteFailure>

    companion object
}

class ClientId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<ClientId>(::ClientId, 1.minLength)
}

class TrackingId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TrackingId>(::TrackingId, 1.minLength)
}
