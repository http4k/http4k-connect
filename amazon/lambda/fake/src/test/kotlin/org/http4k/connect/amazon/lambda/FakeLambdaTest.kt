package org.http4k.connect.amazon.lambda

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.http4k.routing.bind
import org.http4k.routing.functions
import org.http4k.serverless.FnHandler
import org.http4k.serverless.FnLoader
import org.http4k.serverless.InvocationFnLoader
import org.junit.jupiter.api.Test

class FakeLambdaTest : LambdaContract(FakeLambda(reverser)) {
    override val aws = fakeAwsEnvironment

    @Test
    fun `can launch function with FakeLambda and call it as if directly in lambda`() {
        val functions = functions(
            "aFunction" bind FnLoader {
                FnHandler { _: ScheduledEvent, _ ->
                    "aFunction"
                }
            },
            "anApp" bind InvocationFnLoader {
                Response(OK).body(it.bodyString() + it.bodyString())
            }
        )

        val fakeLambda = FakeLambda(functions)
        assertThat(
            fakeLambda(
                Request(Method.POST, "http://localhost/2015-03-31/functions/aFunction/invocations")
                    .body("{}")
            ),
            hasStatus(OK).and(hasBody("aFunction"))
        )

        assertThat(
            fakeLambda(
                Request(Method.POST, "http://localhost/2015-03-31/functions/anApp/invocations")
                    .body("hello")
            ),
            hasStatus(OK).and(hasBody("hellohello"))
        )
    }
}
