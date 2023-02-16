package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.SendRecord
import org.http4k.connect.kafka.httpproxy.action.TopicRecord
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.kafka.httpproxy.model.Offset
import org.http4k.connect.kafka.httpproxy.model.PartitionId
import org.http4k.connect.kafka.httpproxy.model.Topic
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

fun consumeRecords(consumers: Storage<Map<Topic, Int>>, topics: Storage<List<SendRecord>>) =
    "/consumers/{consumerGroup}/instances/{instance}/records" bind GET to { req: Request ->
        val instance = Path.value(ConsumerInstanceId).of("instance")(req)

        consumers[instance]?.let { offsets ->
            val records = offsets
                .flatMap { (topic, originalOffset) ->
                    val allMessages = topics[topic] ?: emptyList()
                    consumers[instance] = consumers[instance]!! + (topic to allMessages.size)
                    allMessages.drop(originalOffset)
                        .mapIndexed { i, it ->
                            it.first to TopicRecord(
                                topic,
                                it.second,
                                it.third,
                                PartitionId.of(0), Offset.of((originalOffset + i).toLong())
                            )
                        }
                }
                .sortedBy { it.first }
                .map { it.second }

            Response(OK)
                .with(Body.auto<List<TopicRecord>>().toLens() of records)

        } ?: Response(NOT_FOUND)
    }
