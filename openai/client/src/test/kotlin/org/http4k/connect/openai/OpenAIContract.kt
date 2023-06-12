package org.http4k.connect.openai

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.startsWith
import org.http4k.connect.openai.ModelName.Companion.GPT3_5
import org.http4k.connect.openai.ModelName.Companion.TEXT_EMBEDDING_ADA_002
import org.http4k.connect.openai.OpenAIOrg.Companion.OPENAI
import org.http4k.connect.openai.Role.Companion.System
import org.http4k.connect.openai.Role.Companion.User
import org.http4k.connect.openai.action.Message
import org.http4k.connect.openai.action.Size
import org.http4k.connect.successValue
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
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
                ),
                1000
            ).successValue().model.value,
            startsWith("gpt-3.5-turbo")
        )
    }

    @Test
    fun `get embeddings`() {
        assertThat(
            openAi.createEmbeddings(
                TEXT_EMBEDDING_ADA_002,
                listOf(Content.of("What is your favourite colour?"))
            ).successValue().model.value,
            startsWith("text-embedding-ada-002")
        )
    }

    @Test
    fun `can generate image`(approver: Approver) {
        openAi.generateImage(Content.of("An excellent library"), Size.`256x256`).successValue()
    }
}
