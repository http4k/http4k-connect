package org.http4k.connect.azure

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThan
import com.natpryce.hamkrest.present
import com.natpryce.hamkrest.startsWith
import org.http4k.connect.azure.ObjectType.Companion.ChatCompletion
import org.http4k.connect.azure.ObjectType.Companion.ChatCompletionChunk
import org.http4k.connect.azure.Role.Companion.System
import org.http4k.connect.azure.Role.Companion.User
import org.http4k.connect.azure.action.Message
import org.http4k.connect.model.ModelName
import org.http4k.connect.successValue
import org.http4k.testing.ApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
interface AzureAIContract {

    val azureAi: AzureAI

    @Test
    fun `get chat response non-stream`() {
        val responses = azureAi.chatCompletion(
            ModelName.GPT3_5,
            listOf(
                Message(System, "You are Leonardo Da Vinci"),
                Message(User, "What is your favourite colour?")
            ),
            1000,
            stream = false
        ).successValue().toList()
        assertThat(responses.size, equalTo(1))
        assertThat(responses.first().usage, present())
        assertThat(responses.first().objectType, equalTo(ChatCompletion))
    }

    @Test
    fun `get chat response streaming`() {
        val responses = azureAi.chatCompletion(
            ModelName.GPT3_5,
            listOf(
                Message(System, "You are Leonardo Da Vinci"),
                Message(User, "What is your favourite colour?")
            ),
            1000,
            stream = true
        ).successValue().toList()
        assertThat(responses.size, greaterThan(0))
        assertThat(responses.first().usage, absent())
        assertThat(responses.first().objectType, equalTo(ChatCompletionChunk))
    }

    @Test
    fun `get embeddings`() {
        assertThat(
            azureAi.createEmbeddings(
                ModelName.TEXT_EMBEDDING_ADA_002,
                listOf("What is your favourite colour?")
            ).successValue().model.value,
            startsWith("text-embedding-ada-002")
        )
    }
}
