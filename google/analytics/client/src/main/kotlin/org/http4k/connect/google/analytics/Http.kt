package org.http4k.connect.google.analytics

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.body.form

fun GoogleAnalytics.Companion.Http(http: HttpHandler, trackingId: TrackingId) = object : GoogleAnalytics {
    override fun pageView(userAgent: String, clientId: ClientId, documentTitle: String, documentPath: String, documentHost: String): Result<Unit, Exception> {
        http(Request(Method.POST, "/collect")
            .header("User-Agent", userAgent)
            .form(VERSION, "1")
            .form(MEASUREMENT_ID, trackingId.value)
            .form(CLIENT_ID, clientId.value)
            .form(DOCUMENT_TITLE, documentTitle)
            .form(DOCUMENT_PATH, documentPath)
            .form(DOCUMENT_HOST, documentHost)
        )
        return Success(Unit)
    }
}

const val VERSION = "v"
const val MEASUREMENT_ID = "tid"
const val CLIENT_ID = "cid"
const val DOCUMENT_TITLE = "dt"
const val DOCUMENT_PATH = "dp"
const val DOCUMENT_HOST = "dh"
