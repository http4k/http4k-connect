package org.http4k.connect

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.format.ConfigurableMoshi
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SystemMoshiContract(
    private val moshi: ConfigurableMoshi,
    vararg actions: Any
) {
    private val obj = actions.toList()

    @Test
    fun `can roundtrip all objects`() {
        obj.forEach {
            assertThat(moshi.asA(moshi.asFormatString(it), it::class), equalTo(it))
        }
    }
}

val randomString = UUID.randomUUID().toString()
