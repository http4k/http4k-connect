package org.http4k.connect.kafka.rest.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure
import org.http4k.connect.kafka.rest.KafkaRestMoshi.asA
import org.http4k.core.Response
import kotlin.reflect.KClass

abstract class AbstractKafkaRestAction<R : Any>(private val clazz: KClass<R>) : Action<Result<R, RemoteFailure>> {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(asA(bodyString().takeIf { it.isNotEmpty() } ?: "{}", clazz))
            else -> Failure(RemoteFailure(toRequest().method, toRequest().uri, status, bodyString()))
        }
    }
}
