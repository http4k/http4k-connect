package org.http4k.connect.google.analytics.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.google.model.ClientId
import org.http4k.connect.google.model.TrackingId
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.body.form

@Http4kConnectAction
data class PageView(
    val userAgent: String,
    val clientId: ClientId,
    val documentTitle: String,
    val documentPath: String,
    val documentHost: String,
    val trackingId: TrackingId
) : GoogleAnalyticsAction<Unit> {
    override fun toRequest() = Request(POST, uri())
        .header("User-Agent", userAgent)
        .form(VERSION, "1")
        .form(MEASUREMENT_ID, trackingId.value)
        .form(CLIENT_ID, clientId.value)
        .form(DOCUMENT_TITLE, documentTitle)
        .form(DOCUMENT_PATH, documentPath)
        .form(DOCUMENT_HOST, documentHost)

    override fun toResult(response: Response) = with(response) {
        if (status.successful) Success(Unit) else Failure(RemoteFailure(POST, uri(), status, bodyString()))
    }

    private fun uri() = Uri.of("/collect")
}

const val VERSION = "v"
const val MEASUREMENT_ID = "tid"
const val CLIENT_ID = "cid"
const val DOCUMENT_TITLE = "dt"
const val DOCUMENT_PATH = "dp"
const val DOCUMENT_HOST = "dh"
