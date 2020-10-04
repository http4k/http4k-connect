package org.http4k.connect.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

interface ExampleContract {
    val http: HttpHandler

    @Test
    fun `can echo`() {
        assertThat(Example.Http(http).echo("hello"), equalTo(Success("hello")))
    }
}

class FakeExampleTest : ExampleContract {
    override val http = FakeExample()
}

@Disabled
class RealExampleTest : ExampleContract {
    override val http = SetBaseUriFrom(Uri.of("http://localhost:9876")).then(JavaHttpClient())
}
