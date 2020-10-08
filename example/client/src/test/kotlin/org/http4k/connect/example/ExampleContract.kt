package org.http4k.connect.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test

interface ExampleContract {
    val http: HttpHandler

    @Test
    fun `can echo`() {
        assertThat(Example.Http(http).echo("hello"), equalTo(Success("hello")))
    }
}
