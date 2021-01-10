package org.http4k.connect.amazon.sqs

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.SQSMessage
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

class FakeSQS(
    queues: Storage<List<SQSMessage>> = Storage.InMemory(),
    clock: Clock = Clock.systemDefaultZone(),
    awsAccount: AwsAccount = AwsAccount.of("1234567890")
) : ChaosFake() {

    override val app = routes(
        "/{account}/{queueName}" bind POST to routes(
            deleteMessage(queues),
            deleteQueue(queues),
            receiveMessage(queues),
            sendMessage(queues)
        ),
        "/" bind POST to createQueue(queues, awsAccount)
    )

    private val client = SQS.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this, clock)

    /**
     * Convenience function to get a SQS client
     */
    fun client() = client
}

fun main() {
    FakeSQS().start()
}
