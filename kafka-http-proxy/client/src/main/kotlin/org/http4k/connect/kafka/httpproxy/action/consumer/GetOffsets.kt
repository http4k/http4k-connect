package org.http4k.connect.kafka.httpproxy.action.consumer

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.model.CommitOffsetsRequest
import org.http4k.connect.kafka.httpproxy.model.GetOffsetsRequest
import org.http4k.connect.kafka.httpproxy.model.PartitionOffsetRequest
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.KAFKA_JSON_V2
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.with

@Http4kConnectAction
data class GetOffsets(val partitions: List<PartitionOffsetRequest>) : KafkaHttpProxyConsumerAction<CommitOffsetsRequest>(kClass()) {
    override fun toRequest() = Request(GET, "/consumers/{group}/instances/{instance}/offsets")
        .with(Body.auto<GetOffsetsRequest>(contentType = ContentType.KAFKA_JSON_V2).toLens() of GetOffsetsRequest(partitions))
}
