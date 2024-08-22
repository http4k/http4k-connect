package org.http4k.connect.azure

import org.http4k.client.JavaHttpClient
import org.http4k.config.Environment.Companion.ENV
import org.http4k.config.EnvironmentKey
import org.http4k.filter.debug
import org.http4k.lens.value
import org.junit.jupiter.api.Assumptions.assumeTrue

class RealAzureAITest : AzureAIContract {
    val apiKey = EnvironmentKey.value(AzureAIApiKey).optional("AZURE_AI_API_KEY")
    val azureHost = EnvironmentKey.value(AzureHost).optional("AZURE_AI_HOST")
    val region = EnvironmentKey.value(Region).optional("AZURE_AI_REGION")

    init {
        assumeTrue(apiKey(ENV) != null, "No API Key set - skipping")
    }

    override val azureAi = AzureAI.Http(
        apiKey(ENV)!!,
        azureHost(ENV)!!,
        region(ENV)!!,
        JavaHttpClient().debug()
    )
}
