package org.http4k.connect.anthropic

import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.azure.AzureAIApiKey
import org.http4k.connect.azure.AzureHost
import org.http4k.connect.azure.Http
import org.http4k.connect.azure.Region
import org.http4k.connect.model.ModelName
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BearerAuth
import org.http4k.routing.Router
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock
import java.time.Clock.systemUTC

class FakeAnthropicAI(
    completionGenerators: Map<ModelName, ChatCompletionGenerator> = emptyMap(),
    clock: Clock = systemUTC()
) : ChaoticHttpHandler() {

    override val app =
        routes(
            BearerAuth { true }
                .then(
                    routes( 
                        Router.orElse bind { req: Request -> Response(Status.OK) }
                    )
                )
        )

    /**
     * Convenience function to get AnthropicAI client
     */
    fun client() = org.http4k.connect.azure.AzureAI.Http(
        AzureAIApiKey.of("azureai-key"),
        AzureHost.of("localhost"),
        Region.of("foo"),
        this
    )
}

fun main() {
    FakeAnthropicAI().start()
}
