package org.http4k.connect.openai

import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.filter.debug
import org.http4k.lens.value
import org.junit.jupiter.api.Assumptions.assumeTrue

class RealOpenAITest : OpenAIContract {
    val apiKey = EnvironmentKey.value(OpenAIToken).optional("OPENAI_KEY")

    init {
        assumeTrue(apiKey(ENV) != null, "No API Key set - skipping")
    }

    override val openAi = OpenAI.Http(
        apiKey(ENV)!!,
        JavaHttpClient().debug(),
        OpenAIOrg.of("org-Ydjc9eGanqJtCP70yPUwZsvs"))
}
