package org.http4k.connect.github

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.squareup.moshi.Moshi
import dev.forkhandles.result4k.Success
import org.http4k.cloudnative.env.Secret
import org.http4k.connect.github.action.GitHubCallbackAction
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.junit.jupiter.api.Test

object GitHubCallbackMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .asConfigurable()
        .withStandardMappings()
        .done()
)

class FooAction : GitHubCallbackAction(CallbackEvent.check_suite, GitHubCallbackMoshi)

class GitHubCallbackTest {
    private val callback = GitHubCallback.Http(
        Uri.of("/foobar"), { Secret("secret") }, { Response(OK) }
    )

    @Test
    fun `send event`() {
        assertThat(callback(FooAction()), equalTo(Success(Unit)))
    }
}
