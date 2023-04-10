package org.http4k.connect.amazon.cloudfront

import de.svenjacobs.loremipsum.LoremIpsum
import org.http4k.connect.openai.Content
import org.http4k.connect.openai.Role.Companion.System
import org.http4k.connect.openai.Role.Companion.User
import org.http4k.connect.openai.action.ChatCompletion
import org.http4k.connect.openai.action.Choice
import org.http4k.connect.openai.action.Message
import kotlin.random.Random

/**
 * Helps to control the generation of responses in a particular format for a model.
 */
fun interface ChatCompletionGenerator : (ChatCompletion) -> List<Choice> {
    companion object
}

/**
 * Simply reverses the input question
 */
val ChatCompletionGenerator.Companion.ReverseInput
    get() = ChatCompletionGenerator { request ->
        listOf(
            Choice(
                0,
                Message(
                    System,
                    Content.of(request.messages.first { it.role == User }.content.value.reversed())
                ), "stop"
            )
        )
    }

val a = LoremIpsum()

/**
 * Generates Lorem Ipsum paragraphs based on the random generator.
 */
fun ChatCompletionGenerator.Companion.LoremIpsum(random: Random = Random(0)) = ChatCompletionGenerator { _ ->
    listOf(
        Choice(
            0,
            Message(
                System,
                Content.of(
                    de.svenjacobs.loremipsum.LoremIpsum().getParagraphs(
                        random.nextInt(3, 15)
                    )
                )
            ), "stop"
        )
    )
}

/**
 * Simply echoes the request
 */
val ChatCompletionGenerator.Companion.Echo
    get() = ChatCompletionGenerator { request ->
        listOf(
            Choice(
                0,
                Message(
                    System,
                    request.messages.first { it.role == User }.content
                ), "stop"
            )
        )
    }
