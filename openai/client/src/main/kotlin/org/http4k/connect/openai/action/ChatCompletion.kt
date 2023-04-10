@file:OptIn(ExperimentalKotshiApi::class)

package org.http4k.connect.openai.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.NonNullAutoMarshalledAction
import org.http4k.connect.kClass
import org.http4k.connect.openai.CompletionId
import org.http4k.connect.openai.Content
import org.http4k.connect.openai.ModelName
import org.http4k.connect.openai.ObjectType
import org.http4k.connect.openai.ObjectType.Companion.ChatCompletion
import org.http4k.connect.openai.OpenAIMoshi
import org.http4k.connect.openai.Role
import org.http4k.connect.openai.Timestamp
import org.http4k.connect.openai.TokenId
import org.http4k.connect.openai.User
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import se.ansman.kotshi.ExperimentalKotshiApi
import se.ansman.kotshi.JsonProperty
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class ChatCompletion(
    val model: ModelName,
    val messages: List<Message>,
    val temperature: Double = 1.0,
    val top_p: Double = 1.0,
    val n: Int = 1,
    val stream: Boolean = false,
    val stop: Any?,
    val max_tokens: Any = "inf",
    val presence_penalty: Double = 0.0,
    val frequency_penalty: Double = 0.0,
    val logit_bias: Map<TokenId, Double>? = null,
    val user: User? = null
) : NonNullAutoMarshalledAction<CompletionResponse>(kClass(), OpenAIMoshi), OpenAIAction<CompletionResponse> {

    constructor(model: ModelName, messages: List<Message>) : this(
        model,
        messages,
        temperature = 1.0,
        top_p = 1.0,
        n = 1,
        stream = false,
        stop = null,
        max_tokens = "inf",
        presence_penalty = 0.0,
        frequency_penalty = 0.0,
        logit_bias = null,
        user = null
    )

    override fun toRequest() = Request(POST, "/v1/chat/completions")
        .with(OpenAIMoshi.autoBody<ChatCompletion>().toLens() of this)
}

@JsonSerializable
data class Message(
    val role: Role, val content: Content, val name: User? = null
)

@JsonSerializable
data class Choice(
    val index: Int, val message: Message, val finish_reason: String
)

@JsonSerializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@JsonSerializable
data class CompletionResponse(
    val id: CompletionId,
    val created: Timestamp,
    val model: ModelName,
    val choices: List<Choice>,
    val usage: Usage
) {
    @JsonProperty(name = "object")
    val objectType: ObjectType = ChatCompletion
}
