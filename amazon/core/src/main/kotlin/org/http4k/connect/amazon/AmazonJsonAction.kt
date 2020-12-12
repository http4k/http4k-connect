package org.http4k.connect.amazon

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.format.AutoMarshalling
import kotlin.reflect.KClass

abstract class AmazonJsonAction<R : Any>(
    private val service: AwsService,
    private val clazz: KClass<R>,
    private val autoMarshalling: AutoMarshalling) : Action<R> {
    override fun toRequest() = Request(POST, Uri.of("/"))
        .header("X-Amz-Target", "${service}.${javaClass.simpleName}")
        .replaceHeader("Content-Type", "application/x-amz-json-1.1")
        .body(autoMarshalling.asFormatString(this))

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(autoMarshalling.asA(bodyString(), clazz))
            else -> Failure(RemoteFailure(POST, Uri.of("/"), status, bodyString()))
        }
    }
}
