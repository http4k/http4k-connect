package org.http4k.connect.kafka.rest.action.consumer

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.rest.model.RecordFormat
import org.http4k.connect.kafka.rest.model.TopicRecord
import org.http4k.core.Method.GET
import org.http4k.core.Request
import java.time.Duration

@Http4kConnectAction
data class ConsumeRecords(
    val format: RecordFormat,
    val timeout: Duration? = null
) : KafkaRestConsumerAction<Array<TopicRecord>>(clazz = kClass()) {
    override fun toRequest() = Request(GET, "/records")
        .header("Accept", format.contentType.value)
}
