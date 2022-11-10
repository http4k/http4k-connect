package org.http4k.connect.google.analytics.ua.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.google.analytics.ua.model.ClientId
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.body.form

@Http4kConnectAction
data class Event(
    val userAgent: String,
    val clientId: ClientId,
    val eventCategory: String,
    val eventAction: String = "",
    val eventLabel: String = "",
    val eventValue: Int? = null
) : GoogleAnalyticsAction<Unit> {
    override fun toRequest() = Request(POST, uri())
        .header("User-Agent", userAgent)
        .form(CLIENT_ID, clientId.value)
        .form(EVENT_TYPE, "event")
        .form(EVENT_CATEGORY, eventCategory)
        .form(EVENT_ACTION, eventAction)
        .form(EVENT_LABEL, eventLabel)
        .run {
            eventValue?.let { form(EVENT_VALUE, it.toString()) } ?: this
        }


    override fun toResult(response: Response) = with(response) {
        if (status.successful) Success(Unit) else Failure(RemoteFailure(POST, uri(), status, bodyString()))
    }

    private fun uri() = Uri.of("/collect")
}

const val EVENT_TYPE = "t"
const val EVENT_ACTION = "ea"
const val EVENT_CATEGORY = "ec"
const val EVENT_LABEL = "el"
const val EVENT_VALUE = "ev"
