package org.http4k.connect.kafka.rest

import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import org.http4k.connect.kafka.rest.action.consumer.KafkaRestConsumerAction
import org.http4k.connect.kafka.rest.model.Consumer
import org.http4k.connect.kafka.rest.model.ConsumerGroup
import org.http4k.connect.kafka.rest.model.ConsumerInstanceId
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.BasicAuth
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.lens.Path
import org.http4k.lens.value

/**
 * Standard HTTP implementation of KafkaRestConsumer
 */
fun KafkaRestConsumer.Companion.Http(
    credentials: Credentials,
    group: ConsumerGroup,
    instance: ConsumerInstanceId,
    baseUri: Uri,
    http: HttpHandler
) = object : KafkaRestConsumer {

    private val http = BasicAuth(credentials)
        .then(SetHostFrom(baseUri))
        .then(http)

    override fun <R : Any> invoke(action: KafkaRestConsumerAction<R>) = action.toResult(
        this.http(
            action.toRequest()
                .with(
                    Path.value(ConsumerGroup).of("group") of group,
                    Path.value(ConsumerInstanceId).of("instance") of instance
                )
        )
    )
}

/**
 * Convenience function to create a consumer
 */
fun KafkaRestConsumer.Companion.Http(
    credentials: Credentials,
    group: ConsumerGroup,
    consumer: Consumer,
    baseUri: Uri,
    http: HttpHandler
) = KafkaRest.Http(credentials, baseUri, http).createConsumer(group, consumer)
    .map { KafkaRestConsumer.Http(credentials, group, it.instance_id, it.base_uri, http) }
    .mapFailure { it.throwIt() }
