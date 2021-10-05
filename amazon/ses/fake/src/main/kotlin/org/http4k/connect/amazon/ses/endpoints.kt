@file:Suppress("FunctionName")

package org.http4k.connect.amazon.ses

import org.http4k.connect.amazon.ses.model.SESMessageId
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.util.*

fun SendEmail() = { r: Request -> r.form("Action") == "SendEmail" }
    .asRouter() bind {
    Response(OK).with(
        viewModelLens of SendEmailResponse(
            SESMessageId.of(
                UUID.randomUUID().toString()
            )
        )
    )
}

val viewModelLens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()
}
