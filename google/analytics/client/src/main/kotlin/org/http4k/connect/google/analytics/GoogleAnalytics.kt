package org.http4k.connect.google.analytics

import dev.forkhandles.result4k.Result

interface GoogleAnalytics {
    fun pageView(userAgent: String, clientId: ClientId, documentTitle: String, documentPath: String, documentHost: String): Result<Unit, Exception>

    companion object
}

data class ClientId(val value: String)
data class TrackingId(val value: String)
