package org.http4k.connect.plugin

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.plugin.bar.testBarAction
import org.http4k.connect.plugin.foo.testFooAction
import org.junit.jupiter.api.Test

class CodeGenerationTest {

    @Test
    fun `correct code is generated for adapter`() {
        assertThat(TestAdapter.Impl().testFooAction("hello"), equalTo(Success("hello")))
        assertThat(TestAdapter.Impl().testBarAction("hello"), equalTo(Success("hello")))
    }

    @Test
    fun `correct code is generated for JSON factory`() {
        assertThat(
            TestMoshi.asFormatString(TestBean("hello")),
            equalTo("""{"value":"hello"}""")
        )
    }
}
