package org.http4k.connect.kafka.rest

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.kafka.rest.action.KafkaRestAction

/**
 * Docs: https://docs.confluent.io/platform/current/kafka-rest/
 */
@Http4kConnectAdapter
interface KafkaRest {
    operator fun <R : Any> invoke(action: KafkaRestAction<R>): Result<R, RemoteFailure>

    companion object
}

