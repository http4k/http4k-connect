package org.http4k.connect.amazon.lambda

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.LambdaName
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test

val reverse = LambdaName("reverse")

abstract class LambdaContract(http: HttpHandler) : AwsContract(AwsService.of("lambda"), http) {

    private val lambda by lazy {
        Lambda.Http(aws.scope, { aws.credentials }, http)
    }

    @Test
    fun `can use echo lambda`() {
        assertThat(lambda.invokeFunction(reverse, "hello"), equalTo(Success("olleh")))
    }
}
