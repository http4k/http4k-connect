package org.http4k.connect.azure

import org.http4k.connect.model.Base64Blob
import org.http4k.connect.model.ModelName
import org.http4k.connect.azure.ObjectType.Companion.ChatCompletion
import org.http4k.connect.azure.ObjectType.Companion.ChatCompletionChunk
import org.http4k.connect.azure.AzureAIMoshi.asFormatString
import org.http4k.connect.azure.AzureAIMoshi.autoBody
import org.http4k.connect.azure.action.ChatCompletion
import org.http4k.connect.azure.action.Choice
import org.http4k.connect.azure.action.CompletionResponse
import org.http4k.connect.azure.action.CreateEmbeddings
import org.http4k.connect.azure.action.Embedding
import org.http4k.connect.azure.action.Embeddings
import org.http4k.connect.azure.action.GenerateImage
import org.http4k.connect.azure.action.GeneratedImage
import org.http4k.connect.azure.action.ImageData
import org.http4k.connect.azure.action.ImageResponseFormat.b64_json
import org.http4k.connect.azure.action.ImageResponseFormat.url
import org.http4k.connect.azure.action.Usage
import org.http4k.connect.model.Timestamp
import org.http4k.core.ContentType.Companion.TEXT_EVENT_STREAM
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.extend
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.static
import java.time.Clock
import java.time.Instant
import java.util.UUID
import kotlin.math.absoluteValue

fun generateImage(clock: Clock, baseUri: Uri) = "/v1/images/generations" bind POST to
    {
        val request = autoBody<GenerateImage>().toLens()(it)

        val logo = request.size.name + ".png"

        Response(OK).with(
            autoBody<GeneratedImage>().toLens() of GeneratedImage(
                Timestamp.of(clock.instant()),
                listOf(
                    when (request.response_format) {
                        url -> ImageData(url = baseUri.extend(Uri.of("/$logo")))
                        b64_json -> ImageData(b64_json = Base64Blob.encode(FakeAzureAI::class.java.getResourceAsStream("/public/$logo")!!))
                    }
                )
            )
        )
    }

fun createEmbeddings() = "/v1/embeddings" bind POST to
    {
        val request = autoBody<CreateEmbeddings>().toLens()(it)

        Response(OK).with(
            autoBody<Embeddings>().toLens() of
                Embeddings(request.input.mapIndexed { index, token ->
                    Embedding(
                        token.split(" ").map { it.hashCode().absoluteValue / 100000000f }.toFloatArray(),
                        index
                    )
                }, request.model, Usage(0, 0, 0))

        )
    }

fun chatCompletion(clock: Clock, completionGenerators: Map<ModelName, ChatCompletionGenerator>) =
    "/v1/chat/completions" bind POST to
        { request ->
            val chatRequest = autoBody<ChatCompletion>().toLens()(request)
            val choices = (completionGenerators[chatRequest.model] ?: ChatCompletionGenerator.LoremIpsum())(chatRequest)

            when {
                chatRequest.stream -> {
                    val parts = choices.mapIndexed { it, choice ->
                        asFormatString(
                            completionResponse(
                                request,
                                it,
                                null,
                                ChatCompletionChunk,
                                chatRequest.model,
                                clock.instant(),
                                listOf(choice)
                            )
                        )
                    } + "[DONE]"
                    Response(OK)
                        .with(CONTENT_TYPE of TEXT_EVENT_STREAM.withNoDirectives())
                        .body(parts.joinToString("\n\n") { "data: $it" }.byteInputStream())
                }

                else -> Response(OK).with(
                    autoBody<CompletionResponse>().toLens() of
                        completionResponse(
                            request,
                            0,
                            Usage(0, 0, 0),
                            ChatCompletion,
                            chatRequest.model,
                            clock.instant(),
                            choices
                        )
                )
            }
        }

private fun completionResponse(
    request: Request,
    it: Int,
    usage: Usage?,
    objectType: ObjectType,
    modelName: ModelName,
    now: Instant,
    choices: List<Choice>
): CompletionResponse = CompletionResponse(
    CompletionId.of(
        UUID.nameUUIDFromBytes((request.bodyString() + "$it").toByteArray()).toString()
    ),
    Timestamp.of(now),
    modelName,
    choices,
    objectType,
    usage
)

fun serveGeneratedContent() = static(Classpath("public"))
