package org.http4k.connect.anthropic

import org.http4k.connect.anthropic.action.AbstractCreateMessage
import org.http4k.connect.anthropic.action.Content
import org.http4k.connect.anthropic.action.CreateMessage
import org.http4k.connect.anthropic.action.CreateMessageStream
import org.http4k.connect.anthropic.action.GeneratedContent
import org.http4k.connect.anthropic.action.GeneratedMessage
import org.http4k.connect.anthropic.action.Usage
import org.http4k.connect.model.Role
import java.util.Random

/**
 * Helps to control the generation of responses in a particular format for a model.
 */
fun interface ChatCompletionGenerator : (AbstractCreateMessage) -> List<GeneratedContent> {
    companion object
}

/**
 * Simply reverses the input question
 */
val ChatCompletionGenerator.Companion.ReverseInput
    get() = ChatCompletionGenerator { req ->
        when (req) {
            is CreateMessage -> req.messages.flatMapIndexed { i, m ->
                listOf(
                    GeneratedMessage(
                        ResponseId.of(i.toString()), Role.Assistant,
                        m.content.mapIndexed { i, m ->
                            Content(Type.text, m.text?.reversed() ?: "")
                        },
                        req.model, StopReason.end_turn, null, Usage(0, 0, 0, 0)
                    )
                )
            }

            is CreateMessageStream -> req.messages.flatMapIndexed { i, m ->
                listOf(
                    GeneratedMessage(
                        ResponseId.of(i.toString()), Role.Assistant,
                        m.content.mapIndexed { i, m ->
                            Content(Type.text, m.text?.reversed() ?: "")
                        },
                        req.model, StopReason.end_turn, null, Usage(0, 0, 0, 0)
                    )
                )
            }
        }
    }

/**
 * Generates Lorem Ipsum paragraphs based on the random generator.
 */
fun ChatCompletionGenerator.Companion.LoremIpsum(random: Random = Random(0)) = ChatCompletionGenerator { req ->
    req.messages.flatMap {
        List(it.content.size) { i ->
            GeneratedMessage(
                ResponseId.of(i.toString()), Role.Assistant,
                listOf(Content(Type.text, de.svenjacobs.loremipsum.LoremIpsum().getParagraphs(random.nextInt(3, 15)))),
                req.model, StopReason.end_turn, null, Usage(0, 0, 0, 0)
            )
        }
    }
}

/**
 * Simply echoes the request
 */
val ChatCompletionGenerator.Companion.Echo
    get() = ChatCompletionGenerator { req ->
        req.messages.flatMap {
            it.content.flatMapIndexed { j, c ->
                List(it.content.size) { i ->
                    GeneratedMessage(
                        ResponseId.of(i.toString()), Role.Assistant,
                        it.content,
                        req.model, StopReason.end_turn, null, Usage(0, 0, 0, 0)
                    )
                }
            }
        }
    }
