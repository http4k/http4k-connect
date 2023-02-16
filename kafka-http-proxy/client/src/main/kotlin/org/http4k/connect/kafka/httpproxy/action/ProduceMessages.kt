package org.http4k.connect.kafka.httpproxy.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.asFormatString
import org.http4k.connect.kafka.httpproxy.model.PartitionOffset
import org.http4k.connect.kafka.httpproxy.model.Records
import org.http4k.connect.kafka.httpproxy.model.SchemaId
import org.http4k.connect.kafka.httpproxy.model.Topic
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class ProduceMessages(val topic: Topic, val records: Records) :
    KafkaHttpProxyAction<ProducedMessages>(kClass()) {

    override fun toRequest() = Request(POST, "/topics/$topic")
        .header("Accept", "application/vnd.kafka.v2+json")
        .with(CONTENT_TYPE of records.contentType())
        .body(asFormatString(records))
}

@JsonSerializable
data class ProducedMessages(
    val key_schema_id: SchemaId?,
    val value_schema_id: SchemaId?,
    val offsets: List<PartitionOffset>
)
