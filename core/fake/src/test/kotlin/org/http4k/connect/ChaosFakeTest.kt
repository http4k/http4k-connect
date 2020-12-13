package org.http4k.connect

import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test

class ChaosFakeTest {

    @Test
    fun `handles spurious errors`() {
        val value = object : ChaosFake() {
            override val app: HttpHandler = { throw UnsupportedOperationException() }
        }
        assertThat(value(Request(Method.GET, "")), hasStatus(INTERNAL_SERVER_ERROR))
    }

}
