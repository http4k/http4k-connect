package org.http4k.connect.github.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure
import org.http4k.core.Response
import org.http4k.format.AutoMarshalling
import org.http4k.format.Moshi
import kotlin.reflect.KClass

abstract class GitHubAction<R : Any>(
    private val clazz: KClass<R>,
    private val autoMarshalling: AutoMarshalling = Moshi
) : Action<Result<R, RemoteFailure>> {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(autoMarshalling.asA(bodyString(), clazz))
            else -> Failure(RemoteFailure(toRequest().method, toRequest().uri, status, bodyString()))
        }
    }
}
