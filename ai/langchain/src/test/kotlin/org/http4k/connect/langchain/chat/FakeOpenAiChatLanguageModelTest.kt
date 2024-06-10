package org.http4k.connect.langchain.chat

import org.http4k.connect.openai.FakeOpenAI
import org.http4k.connect.openai.Http
import org.http4k.connect.openai.OpenAI
import org.http4k.connect.openai.OpenAIToken

class FakeOpenAiChatLanguageModelTest : OpenAIChatLanguageModelContract() {
    override val openAi = OpenAI.Http(OpenAIToken.of("hello"), FakeOpenAI())
}
