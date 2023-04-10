package org.http4k.connect.amazon.cloudfront

import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.openai.Http
import org.http4k.connect.openai.ModelName
import org.http4k.connect.openai.OpenAI
import org.http4k.connect.openai.OpenAIToken
import org.http4k.connect.openai.action.Model
import org.http4k.connect.storage.Storage
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BearerAuth
import org.http4k.routing.routes
import java.time.Clock
import java.time.Clock.systemUTC

class FakeOpenAI(
    models: Storage<Model> = DEFAULT_OPEN_AI_MODELS,
    completionGenerators: Map<ModelName, ChatCompletionGenerator> = emptyMap(),
    clock: Clock = systemUTC()
) : ChaoticHttpHandler() {

    override val app = BearerAuth { true }
        .then(
            routes(
                getModels(models),
                chatCompletion(clock, completionGenerators)
            )
        )

    /**
     * Convenience function to get CloudFront client
     */
    fun client() = OpenAI.Http(OpenAIToken.of("openai-key"))
}


fun main() {
    FakeOpenAI().start()
}
