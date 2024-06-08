package org.http4k.connect.langchain.chat

import dev.forkhandles.result4k.map
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.ImageContent.DetailLevel.AUTO
import dev.langchain4j.data.message.ImageContent.DetailLevel.HIGH
import dev.langchain4j.data.message.ImageContent.DetailLevel.LOW
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.output.FinishReason
import dev.langchain4j.model.output.Response
import dev.langchain4j.model.output.TokenUsage
import org.http4k.connect.openai.OpenAI
import org.http4k.connect.openai.Role
import org.http4k.connect.openai.User
import org.http4k.connect.openai.action.ContentType
import org.http4k.connect.openai.action.Detail.auto
import org.http4k.connect.openai.action.Detail.high
import org.http4k.connect.openai.action.Detail.low
import org.http4k.connect.openai.action.FinishReason.content_filter
import org.http4k.connect.openai.action.FinishReason.length
import org.http4k.connect.openai.action.FinishReason.stop
import org.http4k.connect.openai.action.FinishReason.tool_calls
import org.http4k.connect.openai.action.FunctionSpec
import org.http4k.connect.openai.action.ImageUrl
import org.http4k.connect.openai.action.Message
import org.http4k.connect.openai.action.MessageContent
import org.http4k.connect.openai.action.Tool
import org.http4k.connect.openai.chatCompletion
import org.http4k.connect.orThrow
import org.http4k.core.Uri

fun OpenAiChatLanguageModel(openAi: OpenAI, options: ChatModelOptions = ChatModelOptions()) =
    object : ChatLanguageModel {
        override fun generate(p0: List<ChatMessage>) = generate(p0, emptyList())

        override fun generate(messages: List<ChatMessage>, toolSpecifications: List<ToolSpecification>?)
            : Response<AiMessage> = with(options) {
            openAi.chatCompletion(
                model,
                messages.map {
                    when (it) {
                        is UserMessage -> it.toHttp4k()
                        is SystemMessage -> it.toHttp4k()
                        else -> error("unknown message type")
                    }
                },
                maxTokens,
                temperature,
                top_p,
                n,
                stop,
                presencePenalty,
                frequencyPenalty,
                logitBias,
                user,
                false,
                responseFormat,
                toolSpecifications?.map { it.toHttp4k() },
                toolChoice,
                parallelToolCalls
            )
        }
            .map {
                it.map {
                    Response(
                        AiMessage(it.choices?.mapNotNull { it.message?.content }?.joinToString("") ?: ""),
                        it.usage?.let { TokenUsage(it.prompt_tokens, it.completion_tokens, it.total_tokens) },
                        when (it.choices?.last()?.finish_reason) {
                            stop -> FinishReason.STOP
                            length -> FinishReason.LENGTH
                            content_filter -> FinishReason.CONTENT_FILTER
                            tool_calls -> FinishReason.TOOL_EXECUTION
                            else -> FinishReason.OTHER
                        }
                    )
                }.toList()
            }.orThrow().first()
    }

private fun UserMessage.toHttp4k() = Message(
    Role.User,
    contents().map {
        when (it) {
            is TextContent -> it.toHttp4k()
            is ImageContent -> it.toHttp4k()
            else -> error("unknown content type")
        }
    }, name()?.let { User.of(it) },
    null
)

private fun SystemMessage.toHttp4k() = Message(
    Role.System,
    listOf(MessageContent(ContentType.text, text()))
)

private fun TextContent.toHttp4k() = MessageContent(ContentType.text, text())

private fun ImageContent.toHttp4k() =
    MessageContent(
        ContentType.image_url, null, ImageUrl(
            Uri.of(image().url().toString()),
            when (detailLevel()) {
                LOW -> low
                HIGH -> high
                AUTO -> auto
            }
        )
    )

private fun ToolSpecification.toHttp4k() = Tool(FunctionSpec(name(), parameters(), description()))
