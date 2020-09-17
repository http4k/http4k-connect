package org.http4k.connect.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.Result
import org.junit.jupiter.api.Test

class FakeExampleTest {

    @Test
    fun `can echo`() {
        assertThat(Example.Http(FakeExample()).echo("hello"), equalTo<Result<String, RemoteFailure>>(Success("hello")))
    }
}
