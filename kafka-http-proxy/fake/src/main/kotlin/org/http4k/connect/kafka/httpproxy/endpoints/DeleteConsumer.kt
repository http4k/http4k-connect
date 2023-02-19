package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.CommitState
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.storage.Storage
import org.http4k.connect.storage.get
import org.http4k.connect.storage.set
import org.http4k.core.Method.DELETE
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind

fun deleteConsumer(consumers: Storage<CommitState>) =
    "/consumers/{consumerGroup}/instances/{instance}" bind DELETE to { req: Request ->
        val group = Path.value(ConsumerGroup).of("consumerGroup")(req)
        val instance = Path.value(ConsumerInstanceId).of("instance")(req)
        when {
            consumers[group] == null -> Response(NOT_FOUND)
            else -> when {
                consumers[group]!!.instances.contains(instance) -> {
                    consumers[group] = consumers[group]!!.remove(instance)
                    Response(NO_CONTENT)
                }
                else -> Response(NOT_FOUND)
            }
        }
    }
