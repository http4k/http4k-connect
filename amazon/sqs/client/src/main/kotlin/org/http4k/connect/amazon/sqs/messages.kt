package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.RemoteFailure
import org.http4k.core.ContentType.Companion.APPLICATION_FORM_URLENCODED
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE

data class SendMessage(val payload: String) : SQSAction<Unit> {
    override fun toRequest(): Request {
        val base = listOf(
            "Action" to "SendMessage",
            "MessageBody" to payload,
            "Version" to "2012-11-05"
        )

        val listOf = base + listOf<Pair<String, String>>()

        return listOf.fold(Request(POST, uri())
            .with(CONTENT_TYPE of APPLICATION_FORM_URLENCODED)) { acc, it ->
            acc.form(it.first, it.second)
        }
    }

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(POST, uri(), status))
        }
    }

    private fun uri() = Uri.of("")
}
