package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.CommitState
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.model.CommitOffset
import org.http4k.connect.kafka.httpproxy.model.CommitOffsetsSet
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.PartitionId
import org.http4k.connect.storage.Storage
import org.http4k.connect.storage.get
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind

fun getOffsets(consumers: Storage<CommitState>) =
    "/consumers/{consumerGroup}/instances/{instance}/offsets" bind Method.GET to { req: Request ->
        val group = Path.value(ConsumerGroup).of("consumerGroup")(req)
        consumers[group]
            ?.let {
                Response(OK)
                    .with(Body.auto<CommitOffsetsSet>().toLens() of CommitOffsetsSet(
                        it.offsets.map {
                            CommitOffset(it.key, PartitionId.of(0), it.value.committed)
                        }
                    ))
            }
            ?: Response(NOT_FOUND)
    }
