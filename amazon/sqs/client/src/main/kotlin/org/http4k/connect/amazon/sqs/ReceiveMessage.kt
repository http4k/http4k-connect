package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.QueueName
import org.http4k.core.ContentType.Companion.APPLICATION_FORM_URLENCODED
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import java.time.ZonedDateTime

data class ReceiveMessage(
    val accountId: AwsAccount,
    val queueName: QueueName,
    val MaxNumberOfMessages: Int,
    val VisibilityTimeout: Int,
    val AttributeName: String,
    val Expires: ZonedDateTime
) : SQSAction<List<Message>> {
    override fun toRequest(): Request {
        val base = listOf(
            "Action" to "SendMessage",
            "Version" to "2012-11-05"
        )

        val listOf = base + listOf<Pair<String, String>>()

        return listOf.fold(Request(Method.POST, Uri.of("/$accountId/$queueName"))
            .with(CONTENT_TYPE of APPLICATION_FORM_URLENCODED)) { acc, it ->
            acc.form(it.first, it.second)
        }
    }

    override fun toResult(response: Response): Result<List<Message>, RemoteFailure> {
        TODO("Not yet implemented")
    }

}

data class Message(val payload: String)
