package org.http4k.connect.ollama.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.asRemoteFailure
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.model.ModelName
import org.http4k.connect.ollama.OllamaAction
import org.http4k.connect.ollama.OllamaMoshi
import org.http4k.connect.ollama.OllamaMoshi.autoBody
import org.http4k.connect.ollama.Prompt
import org.http4k.connect.ollama.ResponseFormat
import org.http4k.connect.ollama.SystemMessage
import org.http4k.connect.ollama.Template
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import se.ansman.kotshi.JsonSerializable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant
import kotlin.text.Charsets.UTF_8

@Http4kConnectAction
@JsonSerializable
data class Completion(
    val model: ModelName,
    val prompt: Prompt,
    val images: List<Base64Blob>? = null,
    val stream: Boolean? = false,
    val system: SystemMessage? = null,
    val format: ResponseFormat? = null,
    val template: Template? = null,
    val raw: Boolean? = null,
    val keep_alive: String? = null,
    val options: ModelOptions? = null
) : OllamaAction<Sequence<CompletionResponse>> {

    override fun toRequest() = Request(POST, "/api/generate")
        .with(autoBody<Completion>().toLens() of this)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> when {
                CONTENT_TYPE(response)?.equalsIgnoringDirectives(APPLICATION_JSON) == true ->
                    Success(listOf(autoBody<CompletionResponse>().toLens()(response)).asSequence())

                else -> Success(response.toSequence())
            }

            else -> Failure(asRemoteFailure(this))
        }
    }

    private fun Response.toSequence(): Sequence<CompletionResponse> {
        val reader = BufferedReader(InputStreamReader(body.stream, UTF_8))
        return sequence {
            while (true) {
                yield(OllamaMoshi.asA<CompletionResponse>(reader.readLine() ?: break))
            }
        }
    }
}

@JsonSerializable
data class CompletionResponse(
    val model: ModelName,
    val created_at: Instant,
    val response: String?,
    val done: Boolean,
    val context: List<Long>? = null,
    val total_duration: Long? = null,
    val load_duration: Long? = null,
    val prompt_eval_count: Long? = null,
    val prompt_eval_duration: Long? = null,
    val eval_count: Long? = null,
    val eval_duration: Long? = null
)
