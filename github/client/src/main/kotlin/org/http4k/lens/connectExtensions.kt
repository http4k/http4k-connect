package org.http4k.lens

import org.http4k.connect.github.CallbackEvent
import org.http4k.core.ContentType
import org.http4k.core.HttpMessage

val Header.X_GITHUB_DELIVERY get() = Header.uuid().optional("X-GitHub-Delivery")
val Header.X_GITHUB_EVENT get() = Header.enum<HttpMessage, CallbackEvent>().optional("X-GitHub-Event")
val Header.X_HUB_SIGNATURE_256
    get() = Header
        .map({ it.split("sha256=")[1] }, { "sha256=$it" })
        .optional("X-Hub-Signature-256")

val ContentType.Companion.GITHUB_JSON get() = ContentType("application/vnd.github+json")
