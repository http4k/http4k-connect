package org.http4k.connect.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class HttpExampleTest {

    @Test
    fun `can echo`() {
        assertThat(Example.Http(FakeExample()).echo("hello"), equalTo("hello"))
    }
}
