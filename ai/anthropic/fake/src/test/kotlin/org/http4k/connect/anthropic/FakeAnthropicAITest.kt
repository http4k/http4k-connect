package org.http4k.connect.anthropic

class FakeAnthropicAITest : AnthropicAIContract {
    private val fakeOpenAI = FakeAnthropicAI()
    override val anthropicAi = AnthropicAI.Http(
        AnthropicIApiKey.of("hello"),
        ApiVersion._2023_06_01,
        fakeOpenAI)
}
