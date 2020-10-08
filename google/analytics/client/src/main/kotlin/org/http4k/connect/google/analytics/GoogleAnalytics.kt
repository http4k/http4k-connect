package org.http4k.connect.google.analytics

import dev.forkhandles.result4k.Result
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

data class ClientId(val value: String)
data class TrackingId(val value: String)
