package org.http4k.connect.kafka.rest.partitioning

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class NoOpPartitionerKtTest {
    @Test
    fun `round robins the partitions`() {
        val p = NoOpPartitioner<String, String>(listOf())

        assertThat(p("hello", "world"), equalTo(null))
        assertThat(p("world", "world"), equalTo(null))
    }
}
