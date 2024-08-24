package org.http4k.connect.anthropic

import com.natpryce.hamkrest.greaterThan
import com.natpryce.hamkrest.present
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isA
import org.http4k.connect.anthropic.action.Content
import org.http4k.connect.anthropic.action.GenerationEvent
import org.http4k.connect.anthropic.action.Message
import org.http4k.connect.model.ModelName
import org.http4k.connect.model.Role
import org.http4k.connect.successValue
import org.http4k.testing.ApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
interface AnthropicAIContract {

    val anthropicAi: AnthropicAI

    @Test
    fun `generate message response non-stream`() {
        val responses = anthropicAi.createMessage(
            ModelName.of("claude-3-5-sonnet-20240620"),
            listOf(
                Message(
                    Role.User, listOf(
                        Content(Type.text, "You are Leonardo Da Vinci"),
                    )
                )
            ),
            100,
        ).successValue()

        assertThat(responses.usage.input_tokens!!, greaterThan(0))
    }

    @Test
    fun `generate message response stream`() {
        val responses = anthropicAi.createMessageStream(
            ModelName.of("claude-3-5-sonnet-20240620"),
            listOf(
                Message(
                    Role.User, listOf(Content(Type.text, "You are Leonardo Da Vinci"))
                )
            ),
            100,
            stream = true
        ).successValue().toList()

        assertThat(responses.first(), isA<GenerationEvent.StartMessage>())
    }
}
