package org.http4k.connect.openai

import org.http4k.connect.amazon.cloudfront.FakeOpenAI
import org.http4k.filter.debug

class FakeOpenAITest : OpenAIContract {
    override val openAi = OpenAI.Http(OpenAIToken.of("hello"), FakeOpenAI().debug())
}
