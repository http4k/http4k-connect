package org.http4k.connect.kafka.rest

import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.defaultPort
import org.http4k.chaos.start
import org.http4k.connect.kafka.rest.endpoints.commitOffsets
import org.http4k.connect.kafka.rest.endpoints.consumeRecords
import org.http4k.connect.kafka.rest.endpoints.createConsumer
import org.http4k.connect.kafka.rest.endpoints.deleteConsumer
import org.http4k.connect.kafka.rest.endpoints.getOffsets
import org.http4k.connect.kafka.rest.endpoints.getPartitions
import org.http4k.connect.kafka.rest.endpoints.produceRecords
import org.http4k.connect.kafka.rest.endpoints.seekOffsets
import org.http4k.connect.kafka.rest.endpoints.subscribeToTopics
import org.http4k.connect.kafka.rest.model.ConsumerState
import org.http4k.connect.kafka.rest.model.SendRecord
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Credentials
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BasicAuth
import org.http4k.filter.debug
import org.http4k.routing.bind
import org.http4k.routing.routes

class FakeKafkaRest(
    consumers: Storage<ConsumerState> = Storage.InMemory(),
    topics: Storage<List<SendRecord>> = Storage.InMemory(),
    private val baseUri: Uri = Uri.of("http://localhost:${FakeKafkaRest::class.defaultPort}")
) : ChaoticHttpHandler() {
    override val app = routes(
        BasicAuth("") { true }
            .then(
                routes(
                    subscribeToTopics(consumers),
                    createConsumer(consumers, baseUri),
                    deleteConsumer(consumers),
                    commitOffsets(consumers),
                    getPartitions(),
                    getOffsets(consumers),
                    seekOffsets(consumers),
                    produceRecords(topics),
                    consumeRecords(consumers, topics),
                )
            ),
        "" bind GET to { _ -> Response(OK).body("{}") }
    ).debug()

    /**
     * Convenience function to get a FakeKafkaRest client
     */
    fun client() = KafkaRest.Http(Credentials("", ""), baseUri, this)
}

fun main() {
    FakeKafkaRest().start()
}
