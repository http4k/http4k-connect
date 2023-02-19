package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.model.CommitOffsetsSet
import org.http4k.connect.kafka.httpproxy.model.CommitState
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.storage.Storage
import org.http4k.connect.storage.get
import org.http4k.connect.storage.set
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind

fun setOffsets(consumers: Storage<CommitState>) =
    "/consumers/{consumerGroup}/instances/{instance}/offsets" bind POST to { req: Request ->
        val group = Path.value(ConsumerGroup).of("consumerGroup")(req)

        consumers[group]
            ?.let {
                val offsets = Body.auto<CommitOffsetsSet>().toLens()(req)
                consumers[group] = offsets.offsets.fold(it) { acc, next ->
                    acc.committed(next.topic, next.offset)
                }
                Response(NO_CONTENT)
            }
            ?: Response(NOT_FOUND)
    }

