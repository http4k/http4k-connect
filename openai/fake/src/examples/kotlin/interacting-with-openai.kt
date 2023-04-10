import org.http4k.connect.openai.Content
import org.http4k.connect.openai.Http
import org.http4k.connect.openai.ModelName.Companion.GPT3_5
import org.http4k.connect.openai.OpenAI
import org.http4k.connect.openai.OpenAIToken
import org.http4k.connect.openai.Role.Companion.User
import org.http4k.connect.openai.action.Message
import org.http4k.connect.openai.chatCompletion

fun main() {
    val openAiToken = OpenAIToken.of("your-token-here")
    val openai = OpenAI.Http(openAiToken)

    println(
        openai.chatCompletion(GPT3_5, listOf(Message(User, Content.of("good afternoon"))))
    )
}
