package org.http4k.connect.openai

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.openai.action.ImageResponseFormat.*
import org.http4k.connect.openai.action.Size
import org.http4k.connect.successValue
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
class FakeOpenAITest : OpenAIContract {
    private val fakeOpenAI = FakeOpenAI()
    override val openAi = OpenAI.Http(OpenAIToken.of("hello"), fakeOpenAI)

    @Test
    fun `can generate and server image from url`(approver: Approver) {
        val generated = openAi.generateImage(
            Content.of("An excellent library"), Size.`1024x1024`,
            url, 1.0, null
        ).successValue()

        val uri = generated.data.first().url!!
        assertThat(uri, equalTo(Uri.of("http://localhost:45674/1024x1024.png")))
        approver.assertApproved(fakeOpenAI(Request(GET, uri)))
    }

    @Test
    fun `can generate and server image as data url`(approver: Approver) {
        val generated = openAi.generateImage(
            Content.of("An excellent library"), Size.`1024x1024`,
            b64_json, 1.0, null
        ).successValue()

        approver.assertApproved(generated.data.first().b64_json!!.value)
    }
}
