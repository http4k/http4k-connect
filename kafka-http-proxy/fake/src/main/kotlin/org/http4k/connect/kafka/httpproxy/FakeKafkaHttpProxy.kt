package org.http4k.connect.kafka.httpproxy

import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.defaultPort
import org.http4k.chaos.start
import org.http4k.connect.kafka.httpproxy.endpoints.commitOffsets
import org.http4k.connect.kafka.httpproxy.endpoints.consumeRecords
import org.http4k.connect.kafka.httpproxy.endpoints.createConsumer
import org.http4k.connect.kafka.httpproxy.endpoints.deleteConsumer
import org.http4k.connect.kafka.httpproxy.endpoints.getOffsets
import org.http4k.connect.kafka.httpproxy.endpoints.produceRecords
import org.http4k.connect.kafka.httpproxy.endpoints.subscribeToTopics
import org.http4k.connect.kafka.httpproxy.model.ConsumerState
import org.http4k.connect.kafka.httpproxy.model.SendRecord
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Credentials
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BasicAuth
import org.http4k.routing.routes

class FakeKafkaHttpProxy(
    consumers: Storage<ConsumerState> = Storage.InMemory(),
    topics: Storage<List<SendRecord>> = Storage.InMemory(),
    private val baseUri: Uri = Uri.of("http://localhost:${FakeKafkaHttpProxy::class.defaultPort}")
) : ChaoticHttpHandler() {
    override val app = BasicAuth("") { true }
        .then(
            routes(
                subscribeToTopics(consumers),
                createConsumer(consumers, baseUri),
                deleteConsumer(consumers),
                commitOffsets(consumers),
                getOffsets(consumers),
                produceRecords(topics),
                consumeRecords(consumers, topics)
            )
        )

    /**
     * Convenience function to get a KafkaHttpProxy client
     */
    fun client() = KafkaHttpProxy.Http(Credentials("", ""), baseUri, this)
}

fun main() {
    FakeKafkaHttpProxy().start()
}
