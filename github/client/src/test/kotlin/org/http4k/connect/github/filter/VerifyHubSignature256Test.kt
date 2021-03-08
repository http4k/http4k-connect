package org.http4k.connect.github.filter

import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.cloudnative.env.Secret
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.filter.VerifyGitHubSignatureSha256
import org.http4k.hamkrest.hasStatus
import org.http4k.lens.Header
import org.http4k.lens.X_HUB_SIGNATURE_256
import org.junit.jupiter.api.Test

class VerifyHubSignature256Test {

    private val app = ServerFilters.VerifyGitHubSignatureSha256 { Secret("secret") }.then { Response(OK) }

    @Test
    fun `if valid lets through`() {
        assertThat(
            app(
                Request(POST, "")
                    .body("hello world")
                    .with(Header.X_HUB_SIGNATURE_256 of "734cc62f32841568f45715aeb9f4d7891324e6d948e4c6c60c0621cdac48623a")
            ), hasStatus(OK)
        )
    }

    @Test
    fun `if invalid gives 403`() {
        assertThat(
            app(
                Request(POST, "")
                    .body("goodbye cruel world")
                    .with(Header.X_HUB_SIGNATURE_256 of "734cc62f32841568f45715aeb9f4d7891324e6d948e4c6c60c0621cdac48623a")
            ), hasStatus(FORBIDDEN)
        )
    }
}
