package org.http4k.connect.kafka.rest.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.rest.model.ConsumerGroup
import org.http4k.connect.kafka.rest.model.ConsumerInstanceId
import org.http4k.connect.kafka.rest.model.RecordFormat
import org.http4k.connect.kafka.rest.model.TopicRecord
import org.http4k.core.Method.GET
import org.http4k.core.Request
import java.time.Duration

@Http4kConnectAction
data class ConsumeRecords(
    val group: ConsumerGroup,
    val instance: ConsumerInstanceId,
    val format: RecordFormat,
    val timeout: Duration? = null
) :
    KafkaRestAction<Array<TopicRecord>>(kClass()) {
    override fun toRequest() = Request(GET, "/consumers/$group/instances/$instance/records")
        .header("Accept", format.contentType.value)
}
