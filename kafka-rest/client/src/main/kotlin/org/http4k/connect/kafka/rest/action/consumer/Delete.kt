package org.http4k.connect.kafka.rest.action.consumer

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.kClass
import org.http4k.core.Method.DELETE
import org.http4k.core.Request

@Http4kConnectAction
object Delete : KafkaRestConsumerAction<Unit>(kClass()) {
    override fun toRequest() = Request(DELETE, "/consumers/{group}/instances/{instance}")
}
