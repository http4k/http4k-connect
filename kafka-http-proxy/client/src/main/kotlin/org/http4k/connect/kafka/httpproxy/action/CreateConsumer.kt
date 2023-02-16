package org.http4k.connect.kafka.httpproxy.action

import com.squareup.moshi.Json
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.kafka.httpproxy.model.ConsumerName
import org.http4k.connect.kafka.httpproxy.model.RecordFormat
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.KAFKA_JSON_V2
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.with
import se.ansman.kotshi.JsonSerializable
import java.time.Duration

@Http4kConnectAction
data class CreateConsumer(
    val group: ConsumerGroup,
    val consumer: Consumer,
) : KafkaHttpProxyAction<NewConsumer>(kClass()) {
    override fun toRequest() = Request(POST, "/consumers/$group")
        .with(Body.auto<Consumer>(contentType = ContentType.KAFKA_JSON_V2).toLens() of consumer)
}

@JsonSerializable
data class Consumer(
    val name: ConsumerName,
    val format: RecordFormat,
    @Json(name = "auto.offset.reset") val reset: String,
    @Json(name = "auto.commit.enable") val enableAutocommit: String? = null,
    @Json(name = "fetch.min.bytes") val minBytes: String? = null,
    @Json(name = "consumer.request.timeout.ms") val timeout: Duration? = null
)

@JsonSerializable
data class NewConsumer(val instance_id: ConsumerInstanceId, val base_uri: Uri)
