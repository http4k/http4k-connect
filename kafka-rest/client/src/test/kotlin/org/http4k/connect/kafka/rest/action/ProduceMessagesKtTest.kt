package org.http4k.connect.kafka.rest.action

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.kafka.rest.FakeKafkaRest
import org.http4k.connect.kafka.rest.Http
import org.http4k.connect.kafka.rest.KafkaRest
import org.http4k.connect.kafka.rest.model.Record
import org.http4k.connect.kafka.rest.model.Records
import org.http4k.connect.kafka.rest.model.Topic
import org.http4k.connect.kafka.rest.partitioning.RoundRobinRecordPartitioner
import org.http4k.core.Credentials
import org.http4k.core.Uri
import org.junit.jupiter.api.Test

class ProduceMessagesKtTest {

    @Test
    fun `writing to a list of partitions using a partitioner`() {
        val kafkaRest = KafkaRest.Http(
            Credentials("", ""), Uri.of(""), FakeKafkaRest()
        )

        assertThat(
            kafkaRest.produceMessages(Topic.of("asd"), Records.Json(listOf(Record("123", ""))), ::RoundRobinRecordPartitioner)
                .valueOrNull()!!,
            equalTo(ProducedMessages(null, null, listOf()))
        )
    }
}
