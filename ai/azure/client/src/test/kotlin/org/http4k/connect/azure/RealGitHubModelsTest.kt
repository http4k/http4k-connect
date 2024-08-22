package org.http4k.connect.azure

import org.http4k.client.JavaHttpClient
import org.http4k.config.Environment
import org.http4k.config.EnvironmentKey
import org.http4k.filter.debug
import org.http4k.lens.value
import org.junit.jupiter.api.Assumptions

class RealGitHubModelsTest : AzureAIContract {
    val token = EnvironmentKey.value(GitHubToken).optional("GITHUB_TOKEN")

    init {
        Assumptions.assumeTrue(token(Environment.ENV) != null, "No API Key set - skipping")
    }

    override val azureAi = AzureAI.Http(
        token(Environment.ENV)!!,
        JavaHttpClient().debug()
    )
}
