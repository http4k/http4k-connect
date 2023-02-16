package org.http4k.connect.kafka.httpproxy.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.kafka.httpproxy.model.Offset
import org.http4k.connect.kafka.httpproxy.model.PartitionId
import org.http4k.connect.kafka.httpproxy.model.RecordFormat
import org.http4k.connect.kafka.httpproxy.model.Topic
import org.http4k.core.Method.GET
import org.http4k.core.Request
import se.ansman.kotshi.JsonSerializable
import java.time.Duration

@Http4kConnectAction
data class ConsumeRecords(
    val group: ConsumerGroup,
    val instance: ConsumerInstanceId,
    val format: RecordFormat,
    val timeout: Duration? = null
) :
    KafkaHttpProxyAction<Array<TopicRecord>>(kClass()) {
    override fun toRequest() = Request(GET, "/consumers/$group/instances/$instance/records")
        .header("Accept", format.contentType.value)
}

@JsonSerializable
data class TopicRecord(
    val topic: Topic,
    val key: Any?,
    val `value`: Any,
    val partition: PartitionId,
    val offset: Offset
)
