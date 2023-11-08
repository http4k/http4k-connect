package org.http4k.connect.openai

import org.http4k.connect.openai.Role.Companion.System
import org.http4k.connect.openai.Role.Companion.User
import org.http4k.connect.openai.action.ChatCompletion
import org.http4k.connect.openai.action.Choice
import org.http4k.connect.openai.action.Message
import java.util.Random

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
    get() = ChatCompletionGenerator { req ->
        req.choices(req.messages.first { it.role == User }.content.reversed())
    }

/**
 * Generates Lorem Ipsum paragraphs based on the random generator.
 */
fun ChatCompletionGenerator.Companion.LoremIpsum(random: Random = Random(0)) = ChatCompletionGenerator { req ->
    req.choices(de.svenjacobs.loremipsum.LoremIpsum().getParagraphs(random.nextInt(3, 15)))
}

/**
 * Simply echoes the request
 */
val ChatCompletionGenerator.Companion.Echo
    get() = ChatCompletionGenerator { req ->
        req.choices(req.messages.first { it.role == User }.content)
    }

private fun ChatCompletion.choices(msg: String) = (if (stream) msg.split(" ").map { "$it " } else listOf(msg))
    .map { Choice(0, null, Message(System, it), "stop") }
