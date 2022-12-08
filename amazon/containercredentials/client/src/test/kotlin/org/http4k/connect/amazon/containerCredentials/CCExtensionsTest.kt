package org.http4k.connect.amazon.containerCredentials

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.EMPTY
import org.http4k.connect.amazon.containercredentials.AWS_CONTAINER_CREDENTIALS_FULL_URI
import org.http4k.connect.amazon.containercredentials.AWS_CONTAINER_CREDENTIALS_RELATIVE_URI
import org.http4k.core.Uri
import org.junit.jupiter.api.Test

class EnvironmentUriTest {

    @Test
    fun `uses full uri if present`() {
        val env = Environment.defaults(
            AWS_CONTAINER_CREDENTIALS_FULL_URI of Uri.of("http://foobar/fullpath"),
            AWS_CONTAINER_CREDENTIALS_RELATIVE_URI of Uri.of("randompath")
        )
        assertThat(
            AWS_CONTAINER_CREDENTIALS_FULL_URI[env],
            equalTo(Uri.of("http://foobar"))
        )
    }

    @Test
    fun `constructs uri if only relative present`() {
        val env = Environment.defaults(
            AWS_CONTAINER_CREDENTIALS_FULL_URI of Uri.of("http://foobar/fullpath"),
            AWS_CONTAINER_CREDENTIALS_RELATIVE_URI of Uri.of("randompath")
        )
        assertThat(
            AWS_CONTAINER_CREDENTIALS_FULL_URI[env],
            equalTo(Uri.of("http://169.254.170.2/randompath"))
        )
    }

    @Test
    fun `bloes up if neither present`() {
        val env = EMPTY
        assertThat(
            AWS_CONTAINER_CREDENTIALS_FULL_URI[env],
            equalTo(Uri.of("http://foobar"))
        )
    }
}
