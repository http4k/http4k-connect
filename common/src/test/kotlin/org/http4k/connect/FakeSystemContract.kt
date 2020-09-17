package org.http4k.connect

import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.connect.common.ChaosFake
import org.http4k.core.Request
import org.http4k.core.Status.Companion.I_M_A_TEAPOT
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test

abstract class FakeSystemContract(private val http: ChaosFake) {
    protected abstract val validRequest: Request

    @Test
    fun `returns error when told to misbehave`() {
        val originalStatus = http(validRequest).status
        http.returnStatus(I_M_A_TEAPOT)
        assertThat(http(validRequest), hasStatus(I_M_A_TEAPOT))
        http.behave()
        assertThat(http(validRequest), hasStatus(originalStatus))
    }
}
