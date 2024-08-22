package org.http4k.connect.azure

import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.defaultLocalUri
import org.http4k.chaos.start
import org.http4k.connect.model.ModelName
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BearerAuth
import org.http4k.routing.routes
import java.time.Clock
import java.time.Clock.systemUTC

class FakeAzureAI(
    val completionGenerators: Map<ModelName, ChatCompletionGenerator> = emptyMap(),
    clock: Clock = systemUTC(),
    baseUri: Uri = FakeAzureAI::class.defaultLocalUri,
) : ChaoticHttpHandler() {

    override val app =
        routes(
            BearerAuth { true }
                .then(
                    routes(
                        chatCompletion(clock, completionGenerators),
                        createEmbeddings(),
                        generateImage(clock, baseUri),
                    )
                ),
            serveGeneratedContent(),
        )

    /**
     * Convenience function to get AzureAI client
     */
    fun client() = AzureAI.Http(AzureAIApiKey.of("azureai-key"), AzureHost.of("localhost"), Region.of("foo"), this)
}

fun main() {
    FakeAzureAI().start()
}
