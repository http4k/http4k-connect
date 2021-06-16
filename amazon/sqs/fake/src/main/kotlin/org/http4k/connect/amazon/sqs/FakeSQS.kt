package org.http4k.connect.amazon.sqs

import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

class FakeSQS(
    queues: Storage<List<SQSMessage>> = Storage.InMemory(),
    awsAccount: AwsAccount = AwsAccount.of("1234567890")
) : ChaoticHttpHandler() {

    override val app = routes(
        "/{account}/{queueName}" bind POST to routes(
            deleteMessage(queues),
            deleteQueue(queues),
            receiveMessage(queues),
            sendMessage(queues)
        ),
        "/" bind POST to createQueue(queues, awsAccount)
    )

    /**
     * Convenience function to get a SQS client
     */
    fun client() = SQS.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeSQS().start()
}
