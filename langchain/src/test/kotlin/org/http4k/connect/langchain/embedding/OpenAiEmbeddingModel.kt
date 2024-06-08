package org.http4k.connect.langchain.embedding

import org.http4k.connect.openai.FakeOpenAI
import org.http4k.connect.openai.Http
import org.http4k.connect.openai.OpenAI
import org.http4k.connect.openai.OpenAIToken
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
class OpenAiEmbeddingModelTest {
    private val model = OpenAiEmbeddingModel(OpenAI.Http(OpenAIToken.of("hello"), FakeOpenAI()))

    @Test
    fun `can call through to language model`(approver: Approver) {
        approver.assertApproved(model.embed("hello kitty").content().vectorAsList().toString())
    }
}
