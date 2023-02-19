package org.http4k.connect.kafka.rest

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.kafka.rest.action.consumer.KafkaRestConsumerAction

@Http4kConnectAdapter
interface KafkaRestConsumer {
    operator fun <R : Any> invoke(action: KafkaRestConsumerAction<R>): Result<R, RemoteFailure>

    companion object
}
