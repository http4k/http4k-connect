package org.http4k.connect.amazon.firehose

import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.model.Record
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

class FakeFirehose(records: Storage<List<Record>> = Storage.InMemory()) : ChaoticHttpHandler() {

    private val api = AmazonJsonFake(FirehoseMoshi, Firehose.awsService)

    override val app = routes(
        "/" bind POST to routes(
            api.putRecord(records),
            api.putRecordBatch(records)
        )
    )

    /**
     * Convenience function to get a Firehose client
     */
    fun client() = Firehose.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeFirehose().start()
}
