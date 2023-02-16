package org.http4k.connect.kafka.httpproxy.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.kafka.httpproxy.model.Topic
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.KAFKA_JSON_V2
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class SubscribeToTopics(
    val group: ConsumerGroup,
    val instance: ConsumerInstanceId,
    val topics: List<Topic>,
) : KafkaHttpProxyAction<Unit>(kClass()) {
    override fun toRequest() = Request(POST, "/consumers/$group/instances/$instance/subscription")
        .with(Body.auto<Subscription>(contentType = ContentType.KAFKA_JSON_V2).toLens() of Subscription(topics))
}

@JsonSerializable
data class Subscription(val topics: List<Topic>)
