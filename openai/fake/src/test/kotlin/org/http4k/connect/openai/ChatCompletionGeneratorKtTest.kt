package org.http4k.connect.openai

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.openai.ModelName.Companion.GPT3_5
import org.http4k.connect.openai.Role.Companion.System
import org.http4k.connect.openai.action.ChatCompletion
import org.http4k.connect.openai.action.Choice
import org.http4k.connect.openai.action.Message
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
class ChatCompletionGeneratorKtTest {

    private val input = ChatCompletion(
        GPT3_5, listOf(Message(Role.User, Content.of("foobar")))
    )

    @Test
    fun `lorem ipsum`(approver: Approver) {
        approver.assertApproved(
            ChatCompletionGenerator.LoremIpsum()(input).toString()
        )
    }

    @Test
    fun `reverse input`() {
        assertThat(
            ChatCompletionGenerator.ReverseInput(input),
            equalTo(
                listOf(Choice(0, Message(System, Content.of("raboof")), "stop"))
            )
        )
    }

    @Test
    fun `echo input`() {
        assertThat(
            ChatCompletionGenerator.Echo(input),
            equalTo(
                listOf(Choice(0, Message(System, Content.of("foobar")), "stop"))
            )
        )
    }
}
