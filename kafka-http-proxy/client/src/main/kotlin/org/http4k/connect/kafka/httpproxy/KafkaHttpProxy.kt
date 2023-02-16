package org.http4k.connect.kafka.httpproxy

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.kafka.httpproxy.action.KafkaHttpProxyAction

/**
 * Docs:???
 */
@Http4kConnectAdapter
interface KafkaHttpProxy {
    operator fun <R : Any> invoke(action: KafkaHttpProxyAction<R>): Result<R, RemoteFailure>

    companion object
}

