import dev.forkhandles.result4k.onFailure
import org.http4k.chaos.start
import org.http4k.client.JavaHttpClient
import org.http4k.connect.azure.AzureAI
import org.http4k.connect.azure.AzureAIApiKey
import org.http4k.connect.azure.AzureHost
import org.http4k.connect.azure.FakeAzureAI
import org.http4k.connect.azure.GPT3_5
import org.http4k.connect.azure.Http
import org.http4k.connect.azure.Region
import org.http4k.connect.azure.Role.Companion.User
import org.http4k.connect.azure.action.Message
import org.http4k.connect.azure.action.Size
import org.http4k.connect.azure.chatCompletion
import org.http4k.connect.azure.generateImage
import org.http4k.connect.model.ModelName
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom

fun main() {
    val azureAiApiKey = AzureAIApiKey.of("your-token-here")
    val azureHost = AzureHost.of("your-host")

    // start the fake on the default port
    val port = FakeAzureAI().start().port()

    // create the OpenAI instance pointing to our fake
    val azureAi = AzureAI.Http(
        azureAiApiKey, azureHost, Region.of("region"),
        SetBaseUriFrom(Uri.of("http://localhost:$port"))
            .then(JavaHttpClient())
    )

    // get a chat completion
    azureAi
        .chatCompletion(ModelName.GPT3_5, listOf(Message(User, "good afternoon")), 1000, true)
        .onFailure { error(it) }
        .toList()
        .first()
        .choices
        ?.forEach {
            println(it.message?.role)
            println(it.message?.content)
        }

    // generate an image
    azureAi
        .generateImage("an amazing view", Size.`1024x1024`)
        .onFailure { error(it) }
        .data
        .forEach {
            println("See what I generated at: ${it.url}")
        }
}
