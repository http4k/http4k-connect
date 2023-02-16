package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.action.Subscription
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.kafka.httpproxy.model.Topic
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

fun subscribeToTopics(consumers: Storage<Map<Topic, Int>>) =
    "/consumers/{consumerGroup}/instances/{instance}/subscription" bind POST to { req: Request ->
        val instance = Path.value(ConsumerInstanceId).of("instance")(req)
        consumers[instance]?.let {
            val topics = Body.auto<Subscription>().toLens()(req).topics
            consumers[instance] = topics.fold(it) { acc, next ->
                acc + (next to 0)
            }
            Response(NO_CONTENT)
        } ?: Response(NOT_FOUND)
    }
