package org.http4k.connect.github

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.cloudnative.env.Secret
import org.http4k.connect.github.action.GitHubCallbackAction
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.junit.jupiter.api.Test

class TestCallbackAction : GitHubCallbackAction(CallbackEvent.check_suite)

class GitHubCallbackContract {
    private val callback = GitHubCallback.Http(
        Uri.of("/foobar"), { Secret("secret") }, { Response(OK) }
    )

    @Test
    fun `test callback`() {
        assertThat(callback(TestCallbackAction()), equalTo(Success(Unit)))
    }
}
