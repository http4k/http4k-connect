package org.http4k.connect.github

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.cloudnative.env.Secret
import org.http4k.connect.github.action.GitHubAction
import org.http4k.connect.kClass
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test

class TestAction : GitHubAction<Map<String, String>>(kClass()) {
    override fun toRequest(): Request = Request(POST, "")
}

class GitHubContract {
    private val gitHub = GitHub.Http(
        { Secret("token") }, { Response(OK).body("""{"hello":"world"}""") }
    )

    @Test
    fun `test action`() {
        assertThat(gitHub(TestAction()), equalTo(Success(mapOf("hello" to "world"))))
    }
}
