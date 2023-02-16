package org.http4k.connect.kafka.httpproxy

import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.defaultPort
import org.http4k.chaos.start
import org.http4k.connect.kafka.httpproxy.endpoints.consumeRecords
import org.http4k.connect.kafka.httpproxy.endpoints.createConsumer
import org.http4k.connect.kafka.httpproxy.endpoints.deleteConsumer
import org.http4k.connect.kafka.httpproxy.endpoints.produceMessages
import org.http4k.connect.kafka.httpproxy.endpoints.subscribeToTopics
import org.http4k.connect.kafka.httpproxy.model.Topic
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Credentials
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BasicAuth
import org.http4k.routing.routes

typealias SendRecord = Triple<Long, Any, Any>

class FakeKafkaHttpProxy(
    consumers: Storage<Map<Topic, Int>> = Storage.InMemory(),
    topics: Storage<List<SendRecord>> = Storage.InMemory(),
    baseUri: Uri = Uri.of("http://localhost:${FakeKafkaHttpProxy::class.defaultPort}")
) : ChaoticHttpHandler() {
    override val app = BasicAuth("") { true }
        .then(
            routes(
                subscribeToTopics(consumers),
                createConsumer(consumers, baseUri),
                deleteConsumer(consumers),
                produceMessages(topics),
                consumeRecords(consumers, topics)
            )
        )

    /**
     * Convenience function to get a KafkaHttpProxy client
     */
    fun client() = KafkaHttpProxy.Http(Credentials("", ""), this)
}

fun main() {
    FakeKafkaHttpProxy().start()
}
