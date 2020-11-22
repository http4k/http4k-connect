package org.http4k.connect.google.analytics

import dev.forkhandles.result4k.Result
import dev.forkhandles.values.NonEmptyStringValue
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

class ClientId(value: String) : NonEmptyStringValue(value)
class TrackingId(value: String) : NonEmptyStringValue(value)
