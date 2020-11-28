package org.http4k.connect.amazon

import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.AutoMarshalling
import org.http4k.routing.bind
import org.http4k.routing.header

class AmazonJsonFake(val autoMarshalling: AutoMarshalling, val awsService: AwsService) {
    inline fun <reified Wrapper, reified Req : Any> route(crossinline fn: (Req) -> Any?) =
        header("X-Amz-Target", "${awsService}.${Wrapper::class.simpleName}") bind {
            fn(autoMarshalling.asA(it.bodyString(), Req::class))
                ?.let { Response(Status.OK).body(autoMarshalling.asFormatString(it)) } ?: Response(Status.BAD_REQUEST)
        }
}
