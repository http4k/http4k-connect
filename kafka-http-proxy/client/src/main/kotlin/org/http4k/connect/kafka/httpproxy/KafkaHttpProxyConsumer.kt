package org.http4k.connect.kafka.httpproxy

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.kafka.httpproxy.action.consumer.KafkaHttpProxyConsumerAction

@Http4kConnectAdapter
interface KafkaHttpProxyConsumer {
    operator fun <R : Any> invoke(action: KafkaHttpProxyConsumerAction<R>): Result<R, RemoteFailure>

    companion object
}
