package org.http4k.connect.ollama.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.asRemoteFailure
import org.http4k.connect.ollama.Message
import org.http4k.connect.ollama.ModelName
import org.http4k.connect.ollama.OllamaAction
import org.http4k.connect.ollama.OllamaMoshi
import org.http4k.connect.ollama.OllamaMoshi.autoBody
import org.http4k.connect.ollama.ResponseFormat
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
data class ChatCompletion(
    val model: ModelName,
    val messages: List<Message>,
    val stream: Boolean? = false,
    val format: ResponseFormat? = null,
    val keep_alive: String? = null,
    val options: ModelOptions? = null
) : OllamaAction<Sequence<ChatCompletionResponse>> {

    override fun toRequest() = Request(POST, "/api/chat")
        .with(autoBody<ChatCompletion>().toLens() of this)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> when {
                CONTENT_TYPE(response)?.equalsIgnoringDirectives(APPLICATION_JSON) == true -> Success(
                    listOf(autoBody<ChatCompletionResponse>().toLens()(response)).asSequence()
                )

                else -> Success(response.toSequence())
            }

            else -> Failure(asRemoteFailure(this))
        }
    }

    private fun Response.toSequence(): Sequence<ChatCompletionResponse> {
        val reader = BufferedReader(InputStreamReader(body.stream, UTF_8))
        return sequence {
            while (true) {
                val input = reader.readLine() ?: break
                yield(OllamaMoshi.asA<ChatCompletionResponse>(input))
            }
        }
    }
}

@JsonSerializable
data class ChatCompletionResponse(
    val model: ModelName,
    val created_at: Instant,
    val message: Message?,
    val done: Boolean,
    val total_duration: Long?,
    val load_duration: Long?,
    val prompt_eval_count: Long?,
    val prompt_eval_duration: Long?,
    val eval_count: Long?,
    val eval_duration: Long?
)
