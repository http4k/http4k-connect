package org.http4k.connect.kafka.httpproxy.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.model.CommitOffset
import org.http4k.connect.kafka.httpproxy.model.CommitOffsetsRequest
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with

@Http4kConnectAction
data class CommitOffsets(
    val group: ConsumerGroup,
    val instance: ConsumerInstanceId,
    val offsets: List<CommitOffset>
) : KafkaHttpProxyAction<Unit>(kClass()) {
    override fun toRequest() = Request(POST, "/consumers/$group/instances/$instance/offsets")
        .with(Body.auto<CommitOffsetsRequest>().toLens() of CommitOffsetsRequest(offsets))
}
