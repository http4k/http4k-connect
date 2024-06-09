package org.http4k.connect.langchain.chat

import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.openai.Http
import org.http4k.connect.openai.OpenAI
import org.http4k.connect.openai.OpenAIToken
import org.http4k.lens.value
import org.junit.jupiter.api.Assumptions.assumeTrue

class RealOpenAiChatLanguageModelTest : OpenAIChatLanguageModelContract() {
    val apiKey = EnvironmentKey.value(OpenAIToken).optional("OPEN_AI_TOKEN")

    init {
        assumeTrue(apiKey(ENV) != null, "No API Key set - skipping")
    }

    override val openAi by lazy { OpenAI.Http(apiKey(ENV)!!) }
}
