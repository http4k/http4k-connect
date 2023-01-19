package org.http4k.connect.plugin

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test

class CodeGenerationTest {

    @Test
    fun `correct code is generated`() {
        assertThat(TestAdapter.Impl().testAction("hello"), equalTo(Success("hello")))
    }
}
