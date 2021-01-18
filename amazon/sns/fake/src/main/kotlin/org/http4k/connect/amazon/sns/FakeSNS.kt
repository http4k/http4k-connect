package org.http4k.connect.amazon.sns

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

class FakeSNS(
    topics: Storage<List<SNSMessage>> = Storage.InMemory(),
    awsAccount: AwsAccount = AwsAccount.of("1234567890")
) : ChaosFake() {

    override val app = routes(
        "/" bind POST to routes(
            createTopic(topics, awsAccount),
            deleteTopic(topics),
            listTopics(topics),
            publish(topics)
        )
    )

    /**
     * Convenience function to get a SNS client
     */
    fun client() = SNS.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

data class SNSMessage(val message: String)

fun main() {
    FakeSNS().start()
}
