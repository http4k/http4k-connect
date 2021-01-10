package org.http4k.connect.amazon

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Uri
import org.junit.jupiter.api.Test

class AwsServiceTest {
    @Test
    fun `can construct uri`() {
        assertThat(
            AwsService.of("foobar").toUri(Region.of("eu-west-1")),
            equalTo(Uri.of("https://foobar.eu-west-1.amazonaws.com"))
        )
    }
}
