package org.http4k.connect.amazon.sqs

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.extend
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import java.time.Clock

data class CreateQueueResponse(val url: Uri) : ViewModel
object DeleteQueueResponse : ViewModel

class FakeSQS(
    private val queues: Storage<List<String>> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()) : ChaosFake() {
    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/{account}/{queueName}" bind POST to routes(
            deleteQueue()
        ),
        "/" bind POST to routes(
            createQueue(),
        )
    )

    private fun createQueue() = { r: Request -> r.form("Action") == "CreateQueue" }
        .asRouter() bind { req: Request ->
        val queueName = req.form("QueueName")!!
        if (queues.keySet(queueName).isEmpty()) queues[queueName] = listOf()

        Response(OK).with(lens of CreateQueueResponse(
            req.uri.extend(Uri.of("/1234567890/$queueName"))
        ))
    }

    private fun deleteQueue() = { r: Request -> r.form("Action") == "DeleteQueue" }
        .asRouter() bind { req: Request ->
        val queueName = req.path("queueName")!!

        when {
            queues.keySet(queueName).isEmpty() -> Response(NOT_FOUND)
            else -> Response(OK).with(lens of DeleteQueueResponse)
        }
    }

    /**
     * Convenience function to get a SQS client
     */
    fun client() = SQS.Http(
        AwsCredentialScope("*", "sqs"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeSQS().start()
}
