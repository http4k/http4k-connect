package org.http4k.connect.kafka.httpproxy

import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import org.http4k.connect.kafka.httpproxy.action.consumer.KafkaHttpProxyConsumerAction
import org.http4k.connect.kafka.httpproxy.model.Consumer
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
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
 * Standard HTTP implementation of KafkaHttpProxyConsumer
 */
fun KafkaHttpProxyConsumer.Companion.Http(
    credentials: Credentials,
    group: ConsumerGroup,
    instance: ConsumerInstanceId,
    baseUri: Uri,
    http: HttpHandler
) = object : KafkaHttpProxyConsumer {

    private val http = BasicAuth(credentials)
        .then(SetHostFrom(baseUri))
        .then(http)

    override fun <R : Any> invoke(action: KafkaHttpProxyConsumerAction<R>) = action.toResult(
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
fun KafkaHttpProxyConsumer.Companion.Http(
    credentials: Credentials,
    group: ConsumerGroup,
    consumer: Consumer,
    baseUri: Uri,
    http: HttpHandler
) = KafkaHttpProxy.Http(credentials, baseUri, http).createConsumer(group, consumer)
    .map { KafkaHttpProxyConsumer.Http(credentials, group, it.instance_id, it.base_uri, http) }
    .mapFailure { it.throwIt() }
