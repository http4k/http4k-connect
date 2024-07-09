@file:OptIn(ExperimentalKotshiApi::class)

package org.http4k.connect.openai.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.asRemoteFailure
import org.http4k.connect.model.ModelName
import org.http4k.connect.openai.CompletionId
import org.http4k.connect.openai.ObjectType
import org.http4k.connect.openai.OpenAIAction
import org.http4k.connect.openai.OpenAIMoshi
import org.http4k.connect.openai.OpenAIMoshi.autoBody
import org.http4k.connect.openai.ResponseFormatType
import org.http4k.connect.openai.Role
import org.http4k.connect.openai.Timestamp
import org.http4k.connect.openai.TokenId
import org.http4k.connect.openai.User
import org.http4k.connect.openai.action.Detail.auto
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import se.ansman.kotshi.ExperimentalKotshiApi
import se.ansman.kotshi.JsonProperty
import se.ansman.kotshi.JsonSerializable
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.text.Charsets.UTF_8

@Http4kConnectAction
@JsonSerializable
data class ChatCompletion(
    val model: ModelName,
    val messages: List<Message>,
    val max_tokens: Int? = null,
    val temperature: Double = 1.0,
    val top_p: Double = 1.0,
    val n: Int = 1,
    val stop: Any? = null,
    val presence_penalty: Double = 0.0,
    val frequency_penalty: Double = 0.0,
    val logit_bias: Map<TokenId, Double>? = null,
    val user: User? = null,
    val stream: Boolean = false,
    val response_format: ResponseFormat? = null,
    val tools: List<Tool>? = null,
    val tool_choice: Any? = null,
    val parallel_tool_calls: Boolean? = null,
) : OpenAIAction<Sequence<CompletionResponse>> {
    constructor(model: ModelName, messages: List<Message>, max_tokens: Int = 16, stream: Boolean = true) : this(
        model,
        messages,
        max_tokens = max_tokens,
        temperature = 1.0,
        top_p = 1.0,
        n = 1,
        stop = null,
        presence_penalty = 0.0,
        frequency_penalty = 0.0,
        logit_bias = null,
        user = null,
        stream = stream
    )

    init {
        require(tools == null || tools.isNotEmpty()) { "Tools cannot be empty" }
    }

    override fun toRequest() = Request(POST, "/v1/chat/completions")
        .with(autoBody<ChatCompletion>().toLens() of this)

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
                val line = reader.readLine() ?: break
                if (line.startsWith("data: ")) {
                    when (val chunk = line.removePrefix("data: ").trim()) {
                        "[DONE]" -> break
                        else -> yield(OpenAIMoshi.asA<CompletionResponse>(chunk))
                    }
                }
            }
        }
    }
}

@JsonSerializable
data class ResponseFormat(
    val type: ResponseFormatType
)

@JsonSerializable
data class Message(
    val role: Role?,
    val content: List<MessageContent>,
    val name: User? = null,
    val tool_calls: List<ToolCall>? = null
) {
    constructor(role: Role, text: String, name: User? = null, tool_calls: List<ToolCall>? = null) :
        this(role, listOf(MessageContent(ContentType.text, text)), name, tool_calls)
}

@JsonSerializable
data class Function(val name: String)

@JsonSerializable
data class ToolChoice(val function: Function) {
    val type = "function"
}

@JsonSerializable
data class MessageContent(
    val type: ContentType,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

@JsonSerializable
data class ImageUrl(val url: Uri, val detail: Detail = auto)

enum class Detail {
    low, high, auto
}

enum class ContentType {
    text, image_url
}

@JsonSerializable
data class Choice(
    val index: Int,
    @JsonProperty(name = "message")
    internal val msg: ChoiceDetail?,
    internal val delta: ChoiceDetail?,
    val finish_reason: FinishReason?
) {
    val message get() = msg ?: delta
}

enum class FinishReason {
    stop, length, content_filter, tool_calls
}

@JsonSerializable
data class ChoiceDetail(
    val role: Role? = null,
    val content: String? = null,
    val tool_calls: List<ToolCall>? = null,
)

@JsonSerializable
data class ToolCall(
    val id: String,
    val type: String,
    val function: FunctionCall,
    val index: Int? = null
)

@JsonSerializable
data class Tool(val function: FunctionSpec) {
    val type = "function"
}

@JsonSerializable
data class FunctionSpec(
    val name: String,
    val parameters: Any? = null, // JSON schema format
    val description: String? = null,
) {
    val type = "function"
}

@JsonSerializable
data class FunctionCall(
    val name: String,
    val arguments: String
)

@JsonSerializable
data class CompletionResponse(
    val id: CompletionId,
    val created: Timestamp,
    val model: ModelName,
    val choices: List<Choice>? = null,
    @JsonProperty(name = "object")
    val objectType: ObjectType,
    val usage: Usage? = null,
)
