package org.http4k.connect.amazon.lambda

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.core.model.FunctionName
import org.http4k.connect.amazon.lambda.action.invokeFunction
import org.http4k.core.HttpHandler
import org.http4k.format.Moshi
import org.junit.jupiter.api.Test

val reverse = FunctionName("reverse")

abstract class LambdaContract(http: HttpHandler) : AwsContract() {

    private val lambda by lazy {
        Lambda.Http(aws.region, { aws.credentials }, http)
    }

    @Test
    fun `can use echo lambda`() {
        assertThat(lambda.invokeFunction(reverse, "hello", Moshi), equalTo(Success("olleh")))
    }
}
