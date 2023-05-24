package org.http4k.connect.openai.auth

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.openai.auth.PluginToken
import org.http4k.connect.openai.auth.PluginToken.Basic
import org.http4k.connect.openai.auth.PluginToken.Bearer
import org.http4k.core.Credentials
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.lens.Header
import org.http4k.security.AccessToken
import org.junit.jupiter.api.Test

class PluginTokenTest {

    private val validCreds = Credentials("user", "password")
    private val validToken = AccessToken("foobar")

    @Test
    fun `test basic auth`() {
        val validRequest = Request(GET, "")
            .with(Header.AUTHORIZATION_BASIC of validCreds)

        Basic("realm") { it == validCreds }
            .assertAuthsOk(validRequest)
    }

    @Test
    fun `test bearer auth`() {
        val validRequest = Request(GET, "")
            .header("Authorization", "Bearer " + validToken.value)
        Bearer { it == validToken }.assertAuthsOk(validRequest)
    }

    private fun PluginToken.assertAuthsOk(req: Request) {
        val withRC = securityFilter.then { Response(OK) }

        assertThat(withRC(req.removeHeaders()).status, equalTo(UNAUTHORIZED))
        assertThat(withRC(req).status, equalTo(OK))
    }
}
