package org.http4k.connect.openai

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.openai.ModelName.Companion.GPT3_5
import org.http4k.connect.openai.OpenAIOrg.Companion.OPENAI
import org.http4k.connect.openai.Role.Companion.System
import org.http4k.connect.openai.Role.Companion.User
import org.http4k.connect.openai.action.Message
import org.http4k.connect.successValue
import org.junit.jupiter.api.Test

interface OpenAIContract {

    val openAi: OpenAI

    @Test
    fun `get models`() {
        assertThat(
            openAi.getModels().successValue().data
                .first { it.id == ObjectId.of("text-babbage-001") }.owned_by,
            equalTo(OPENAI)
        )
    }

    @Test
    fun `get chat response`() {
        assertThat(
            openAi.chatCompletion(
                GPT3_5,
                listOf(
                    Message(System, Content.of("You are Leonado Da Vinci")),
                    Message(User, Content.of("What is your favourite colour?"))
                )
            ).successValue().model,
            equalTo(GPT3_5)
        )
    }
}
