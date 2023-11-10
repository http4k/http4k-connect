package org.http4k.connect.amazon.model

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import dev.forkhandles.values.parseOrNull
import org.junit.jupiter.api.Test

class EventDetailTest {

    @Test
    fun `enforces pattern`() {
        assertThat(EventDetail.parseOrNull("""{"last_name": "Moll "}"""), present())
        assertThat(EventDetail.parseOrNull("{}"), present())
        assertThat(EventDetail.parseOrNull("""{"123":"123""}"""), present())
        assertThat(EventDetail.parseOrNull("123"), absent())
    }
}
