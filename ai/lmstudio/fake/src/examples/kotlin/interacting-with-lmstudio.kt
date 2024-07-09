import org.http4k.connect.lmstudio.CHAT_MODEL
import org.http4k.connect.lmstudio.Http
import org.http4k.connect.lmstudio.LmStudio
import org.http4k.connect.lmstudio.Role.Companion.User
import org.http4k.connect.lmstudio.action.Message
import org.http4k.connect.lmstudio.chatCompletion
import org.http4k.connect.model.ModelName

fun main() {
    // create a client
    val lmstudio = LmStudio.Http()

    // get a chat completion
    println(
        lmstudio.chatCompletion(
            ModelName.CHAT_MODEL,
            listOf(
                Message(User, "Explain pythagoras's theorem to a 5 year old child"),
            ),
            1000,
            false
        )
    )
}
