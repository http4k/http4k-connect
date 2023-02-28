package org.http4k.connect.kafka.rest.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.connect.kafka.rest.KafkaRestMoshi.auto
import org.http4k.connect.kafka.rest.model.Consumer
import org.http4k.connect.kafka.rest.model.ConsumerGroup
import org.http4k.connect.kafka.rest.model.ConsumerInstance
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.KAFKA_JSON_V2
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.with
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class CreateConsumer(
    val group: ConsumerGroup,
    val consumer: Consumer,
) : KafkaRestAction<NewConsumer>(kClass()) {
    override fun toRequest() = Request(POST, "/consumers/$group")
        .with(Body.auto<Consumer>(contentType = ContentType.KAFKA_JSON_V2).toLens() of consumer)
}

@JsonSerializable
data class NewConsumer(val instance_id: ConsumerInstance, val base_uri: Uri)
