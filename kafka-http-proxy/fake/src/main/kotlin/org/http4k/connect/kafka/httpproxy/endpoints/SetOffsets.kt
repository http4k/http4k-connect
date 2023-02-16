package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.CommitState
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.model.CommitOffsetsRequest
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
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
        val instance = Path.value(ConsumerInstanceId).of("instance")(req)

        consumers[instance]
            ?.let {
                val offsets = Body.auto<CommitOffsetsRequest>().toLens()(req)
                consumers[instance] = offsets.offsets.fold(it) { acc, next ->
                    acc.updated(next.topic, next.offset)
                }
                Response(NO_CONTENT)
            }
            ?: Response(NOT_FOUND)
    }
