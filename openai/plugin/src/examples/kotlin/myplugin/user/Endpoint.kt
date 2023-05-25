package myplugin.user

import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Path

/**
 * A standard http4k contract endpoint
 */
fun greetingEndpoint() = Path.of("first") / Path.of("second") meta {
    summary = "A great api endpoint"
} bindContract GET to
    { first, second ->
        { _: Request -> Response(OK).body("hello $first $second !") }
    }
