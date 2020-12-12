package org.http4k.connect.google.analytics

import dev.forkhandles.result4k.Result
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength
import org.http4k.connect.RemoteFailure

data class PageView(
    val userAgent: String,
    val clientId: ClientId,
    val documentTitle: String,
    val documentPath: String,
    val documentHost: String
)

interface GoogleAnalytics {
    operator fun invoke(request: PageView): Result<Unit, RemoteFailure>

    companion object
}

class ClientId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<ClientId>(::ClientId, 1.minLength)
}

class TrackingId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TrackingId>(::TrackingId, 1.minLength)
}
