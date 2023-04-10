package org.http4k.connect.amazon.cloudfront

import org.http4k.connect.openai.CompletionId
import org.http4k.connect.openai.Content
import org.http4k.connect.openai.OpenAIMoshi.autoBody
import org.http4k.connect.openai.Role.Companion.System
import org.http4k.connect.openai.Role.Companion.User
import org.http4k.connect.openai.Timestamp
import org.http4k.connect.openai.action.ChatCompletion
import org.http4k.connect.openai.action.Choice
import org.http4k.connect.openai.action.Completion
import org.http4k.connect.openai.action.Message
import org.http4k.connect.openai.action.Model
import org.http4k.connect.openai.action.Models
import org.http4k.connect.openai.action.Usage
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.bind
import java.time.Clock
import java.util.UUID

fun getModels(models: Storage<Model>) = "/v1/models" bind GET to
    {
        Response(OK).with(
            autoBody<Models>().toLens() of
                Models(models.keySet().map { models[it]!! })
        )
    }

fun chatCompletion(clock: Clock) = "/v1/chat/completions" bind POST to
    {
        val request: ChatCompletion = autoBody<ChatCompletion>().toLens()(it)
        Response(OK).with(
            autoBody<Completion>().toLens() of
                Completion(
                    CompletionId.of(
                        UUID.nameUUIDFromBytes(it.bodyString().toByteArray()).toString()
                    ),
                    Timestamp.of(clock.instant()),
                    request.model,
                    listOf(
                        Choice(
                            0,
                            Message(
                                System,
                                Content.of(request.messages.first { it.role == User }.content.value.reversed())
                            ), "stop"
                        )
                    ),
                    Usage(0, 0, 0)
                )
        )
    }
