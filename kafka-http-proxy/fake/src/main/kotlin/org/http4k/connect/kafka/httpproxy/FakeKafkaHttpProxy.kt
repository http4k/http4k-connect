package org.http4k.connect.kafka.httpproxy

import dev.forkhandles.values.ZERO
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.defaultPort
import org.http4k.chaos.start
import org.http4k.connect.kafka.httpproxy.endpoints.consumeRecords
import org.http4k.connect.kafka.httpproxy.endpoints.createConsumer
import org.http4k.connect.kafka.httpproxy.endpoints.deleteConsumer
import org.http4k.connect.kafka.httpproxy.endpoints.getOffsets
import org.http4k.connect.kafka.httpproxy.endpoints.produceMessages
import org.http4k.connect.kafka.httpproxy.endpoints.setOffsets
import org.http4k.connect.kafka.httpproxy.endpoints.subscribeToTopics
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.kafka.httpproxy.model.Offset
import org.http4k.connect.kafka.httpproxy.model.Topic
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Credentials
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BasicAuth
import org.http4k.routing.routes

typealias SendRecord = Triple<Long, Any?, Any>

data class TopicCommitState(
    val next: Offset = Offset.ZERO,
    val committed: Offset = Offset.ZERO
) {
    fun next(new: Offset) = TopicCommitState(new, committed)
    fun committed(new: Offset) = TopicCommitState(new, new)
}

data class CommitState(
    val instances: Set<ConsumerInstanceId>,
    val auto: Boolean,
    val offsets: Map<Topic, TopicCommitState>
) {
    fun add(instance: ConsumerInstanceId) = copy(instances = instances + instance)
    fun remove(instance: ConsumerInstanceId) = copy(instances = instances - instance)
    fun next(topic: Topic, new: Offset) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()).next(new))
        )

    fun committed(topic: Topic, new: Offset) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()).committed(new))
        )
}

class FakeKafkaHttpProxy(
    consumers: Storage<CommitState> = Storage.InMemory(),
    topics: Storage<List<SendRecord>> = Storage.InMemory(),
    private val baseUri: Uri = Uri.of("http://localhost:${FakeKafkaHttpProxy::class.defaultPort}")
) : ChaoticHttpHandler() {
    override val app = BasicAuth("") { true }
        .then(
            routes(
                subscribeToTopics(consumers),
                createConsumer(consumers, baseUri),
                deleteConsumer(consumers),
                setOffsets(consumers),
                getOffsets(consumers),
                produceMessages(topics),
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
