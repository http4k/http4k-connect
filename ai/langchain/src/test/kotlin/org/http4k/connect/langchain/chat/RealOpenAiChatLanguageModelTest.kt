package org.http4k.connect.langchain.chat

import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.openai.Http
import org.http4k.connect.openai.OpenAI
import org.http4k.connect.openai.OpenAIToken
import org.http4k.filter.debug
import org.http4k.lens.value
import org.junit.jupiter.api.Assumptions.assumeTrue

class RealOpenAiChatLanguageModelTest : ChatLanguageModelContract {

    val apiKey = EnvironmentKey.value(OpenAIToken).optional("OPEN_AI_TOKEN")

    init {
        assumeTrue(apiKey(ENV) != null, "No API Key set - skipping")
    }

    override val model by lazy {
        OpenAiChatLanguageModel(
            OpenAI.Http(apiKey(ENV)!!, JavaHttpClient().debug()),
            ChatModelOptions(temperature = 0.0)
        )
    }
}
