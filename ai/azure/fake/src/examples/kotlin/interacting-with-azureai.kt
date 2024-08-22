import org.http4k.config.Environment.Companion.ENV
import org.http4k.config.EnvironmentKey
import org.http4k.connect.model.ModelName
import org.http4k.connect.azure.GPT3_5
import org.http4k.connect.azure.Http
import org.http4k.connect.azure.AzureAI
import org.http4k.connect.azure.AzureAIApiKey
import org.http4k.connect.azure.AzureHost
import org.http4k.connect.azure.Region
import org.http4k.connect.azure.Role.Companion.User
import org.http4k.connect.azure.action.Message
import org.http4k.connect.azure.chatCompletion
import org.http4k.lens.value

fun main() {
    val apiKey = EnvironmentKey.value(AzureAIApiKey).required("AZURE_AI_API_KEY")(ENV)
    val azureHost = EnvironmentKey.value(AzureHost).required("AZURE_AI_HOST")(ENV)
    val region = EnvironmentKey.value(Region).required("AZURE_AI_REGION")(ENV)

    // create a client
    val azureAi = AzureAI.Http(apiKey, azureHost, region)

    // get a chat completion
    println(
        azureAi.chatCompletion(
            ModelName.GPT3_5,
            listOf(
                Message(User, "Explain pythagoras's theorem to a 5 year old child"),
            ),
            1000,
            false
        )
    )
}
