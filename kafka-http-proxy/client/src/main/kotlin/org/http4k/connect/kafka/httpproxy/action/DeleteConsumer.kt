package org.http4k.connect.kafka.httpproxy.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.core.Method.DELETE
import org.http4k.core.Request

@Http4kConnectAction
data class DeleteConsumer(
    val group: ConsumerGroup,
    val instance: ConsumerInstanceId,
) : KafkaHttpProxyAction<Unit>(kClass()) {
    override fun toRequest() = Request(DELETE, "/consumers/$group/instances/$instance")
}
