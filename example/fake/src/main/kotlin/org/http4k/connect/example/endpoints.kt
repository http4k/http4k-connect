package org.http4k.connect.example

import org.http4k.connect.storage.Storage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import java.util.UUID

fun echo(echoes: Storage<String>) = "/echo" bind Method.POST to { req: Request ->
    echoes[UUID.randomUUID().toString()] = req.bodyString()
    Response(Status.OK).body(req.body)
}

fun reverse() = "/reverse" bind Method.POST to { req: Request ->
    Response(Status.OK).body(req.bodyString().reversed())
}
