package org.http4k.connect.amazon.sts

import org.http4k.base64Encode
import org.http4k.connect.ChaosFake
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.time.Clock
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID.randomUUID

class FakeSTS(private val clock: Clock = Clock.systemDefaultZone(),
              private val defaultSessionValidity: Duration = Duration.ofHours(1)
) : ChaosFake() {
    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/" bind POST to routes(
            assumeRole()
        )
    )

    private fun assumeRole() = { r: Request -> r.form("Action") == "AssumeRole" }
        .asRouter() bind { req: Request ->
        val duration = req.form("DurationSeconds")
            ?.toLong()
            ?.let { Duration.ofSeconds(it) }
            ?: defaultSessionValidity
        Response(OK).with(lens of AssumeRoleResponse(
            req.form("RoleArn")!!,
            req.form("RoleSessionName")!!,
            "accessKeyId",
            "secretAccessKey",
            randomUUID().toString().base64Encode(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").format(
                ZonedDateTime.now(clock) + duration)
        ))
    }
}

fun main() {
    FakeSTS().start()
}
