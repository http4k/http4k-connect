package org.http4k.connect.example

import org.http4k.client.JavaHttpClient
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach

class RealExampleTest : ExampleContract {

    @BeforeEach
    fun setup() {
        // this should auto-detect any configuration via assume()
        Assumptions.assumeTrue(false)
    }

    override val http = ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:9876")).then(JavaHttpClient())
}
