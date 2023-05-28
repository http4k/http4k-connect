package org.http4k.connect.openai

import org.http4k.connect.model.Base64Blob
import org.http4k.connect.openai.OpenAIMoshi.autoBody
import org.http4k.connect.openai.action.ChatCompletion
import org.http4k.connect.openai.action.CompletionResponse
import org.http4k.connect.openai.action.CreateEmbeddings
import org.http4k.connect.openai.action.Embedding
import org.http4k.connect.openai.action.Embeddings
import org.http4k.connect.openai.action.GenerateImage
import org.http4k.connect.openai.action.GeneratedImage
import org.http4k.connect.openai.action.ImageData
import org.http4k.connect.openai.action.ImageResponseFormat.b64_json
import org.http4k.connect.openai.action.ImageResponseFormat.url
import org.http4k.connect.openai.action.Model
import org.http4k.connect.openai.action.Models
import org.http4k.connect.openai.action.Usage
import org.http4k.connect.openai.auth.OpenAIPluginId
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.extend
import org.http4k.core.with
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.static
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import java.time.Clock
import java.util.UUID
import kotlin.math.absoluteValue

fun generateImage(clock: Clock, baseUri: Uri) = "/v1/images/generations" bind POST to
    {
        val request = autoBody<GenerateImage>().toLens()(it)

        val image = "/${request.size.name + ".png"}"

        Response(OK).with(
            autoBody<GeneratedImage>().toLens() of GeneratedImage(
                Timestamp.of(clock.instant()),
                listOf(
                    when (request.response_format) {
                        url -> {
                            ImageData(url = baseUri.extend(Uri.of(image)))
                        }

                        b64_json -> ImageData(b64_json = Base64Blob.encode(FakeOpenAI::class.java.getResourceAsStream("/public/$image")!!))
                    }
                )
            )
        )
    }

fun getModels(models: Storage<Model>) = "/v1/models" bind GET to
    {
        Response(OK).with(
            autoBody<Models>().toLens() of
                Models(models.keySet().map { models[it]!! })
        )
    }

fun createEmbeddings(models: Storage<Model>) = "/v1/embeddings" bind POST to
    {
        val request = autoBody<CreateEmbeddings>().toLens()(it)

        models[request.model.value]?.let {
            Response(OK).with(
                autoBody<Embeddings>().toLens() of
                    Embeddings(request.input.mapIndexed { index, token ->
                        Embedding(token.value.split(" ").map { it.hashCode().absoluteValue / 100000000f }, index)
                    }, request.model, Usage(0, 0, 0))

            )
        } ?: Response(NOT_FOUND)
    }

fun chatCompletion(clock: Clock, completionGenerators: Map<ModelName, ChatCompletionGenerator>) =
    "/v1/chat/completions" bind POST to
        {
            val request = autoBody<ChatCompletion>().toLens()(it)
            Response(OK).with(
                autoBody<CompletionResponse>().toLens() of
                    CompletionResponse(
                        CompletionId.of(
                            UUID.nameUUIDFromBytes(it.bodyString().toByteArray()).toString()
                        ),
                        Timestamp.of(clock.instant()),
                        request.model,
                        (completionGenerators[request.model] ?: ChatCompletionGenerator.LoremIpsum())(request),
                        Usage(0, 0, 0)
                    )
            )
        }

fun serveGeneratedContent() = static(Classpath("public"))

fun index(plugins: List<OpenAIPluginId>): RoutingHttpHandler {
    val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), TEXT_HTML).toLens()

    return "/" bind GET to { Response(OK).with(lens of Index(plugins)) }
}

data class Index(val plugins: List<OpenAIPluginId>) : ViewModel
