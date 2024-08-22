package org.http4k.connect.azure

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.azure.action.ImageResponseFormat.b64_json
import org.http4k.connect.azure.action.ImageResponseFormat.url
import org.http4k.connect.azure.action.Size
import org.http4k.connect.successValue
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.testing.Approver
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.Test

class FakeAzureAITest : AzureAIContract {
    private val fakeOpenAI = FakeAzureAI()
    override val azureAi = AzureAI.Http(AzureAIApiKey.of("hello"),
        AzureHost.of("foobar"), Region.of("barfoo"),
        fakeOpenAI)

    @Test
    fun `can generate and serve image from url`(approver: Approver) {
        val generated = azureAi.generateImage(
            "An excellent library", Size.`1024x1024`,
            url, 1, Quality.standard, Style.vivid, null
        ).successValue()

        val uri = generated.data.first().url!!
        assertThat(uri, equalTo(Uri.of("http://localhost:14504/1024x1024.png")))
        approver.assertApproved(fakeOpenAI(Request(GET, uri)))
    }

    @Test
    fun `can generate and serve image as data url`(approver: Approver) {
        val generated = azureAi.generateImage(
            "An excellent library", Size.`1024x1024`,
            b64_json, 1, Quality.standard, Style.vivid, null
        ).successValue()

        approver.assertApproved(generated.data.first().b64_json!!.value)
    }
}
