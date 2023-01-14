package org.http4k.connect.plugin

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.valueOrNull
import org.junit.jupiter.api.Test

class AdapterGeneratedTest {

    @Test
    fun `adapter actions are generated`() {
        assertThat(TestAdapter.Companion.Impl()(TestAction("hello")).valueOrNull(), equalTo("hello"))
    }
}
