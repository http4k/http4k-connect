package org.http4k.connect.langchain.chat

import org.http4k.connect.model.ModelName
import org.http4k.connect.openai.GPT3_5
import org.http4k.connect.openai.TokenId
import org.http4k.connect.openai.User
import org.http4k.connect.openai.action.ResponseFormat

data class ChatModelOptions(
    val model: ModelName = ModelName.GPT3_5,
    val stream: Boolean? = null,
    val maxTokens: Int? = null,
    val temperature: Double = 1.0,
    val top_p: Double = 1.0,
    val n: Int = 1,
    val stop: Any? = null,
    val presencePenalty: Double = 0.0,
    val frequencyPenalty: Double = 0.0,
    val logitBias: Map<TokenId, Double>? = null,
    val user: User? = null,
    val responseFormat: ResponseFormat? = null,
    val toolChoice: Any? = null,
    val parallelToolCalls: Boolean? = null,
)
