package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.CommitState
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.SendRecord
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.Offset
import org.http4k.connect.kafka.httpproxy.model.PartitionId
import org.http4k.connect.kafka.httpproxy.model.TopicRecord
import org.http4k.connect.storage.Storage
import org.http4k.connect.storage.get
import org.http4k.connect.storage.set
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind

fun consumeRecords(consumers: Storage<CommitState>, topics: Storage<List<SendRecord>>) =
    "/consumers/{consumerGroup}/instances/{instance}/records" bind GET to { req: Request ->
        val group = Path.value(ConsumerGroup).of("consumerGroup")(req)

        consumers[group]?.let { state ->
            val offsets = state.offsets
            val records = offsets
                .flatMap { (topic, originalTopicState) ->
                    val allMessages = topics[topic] ?: emptyList()

                    consumers[group] = consumers[group]!!.next(topic, Offset.of(allMessages.size.toLong()))

                    if (consumers[group]!!.auto) {
                        consumers[group] = consumers[group]!!.committed(topic, Offset.of(allMessages.size.toLong()))
                    }
                    allMessages.drop(originalTopicState.next.value.toInt())
                        .mapIndexed { i, it ->
                            it.first to TopicRecord(
                                topic,
                                it.second,
                                it.third,
                                PartitionId.of(0), Offset.of((originalTopicState.next.value + i))
                            )
                        }
                }
                .sortedBy { it.first }
                .map { it.second }

            Response(OK)
                .with(Body.auto<List<TopicRecord>>().toLens() of records)

        } ?: Response(NOT_FOUND)
    }
