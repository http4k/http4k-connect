package org.http4k.connect.kafka.httpproxy

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.asFormatString
import org.http4k.connect.kafka.httpproxy.model.AvroRecord
import org.http4k.connect.kafka.httpproxy.model.BinaryRecord
import org.http4k.connect.kafka.httpproxy.model.CommitOffset
import org.http4k.connect.kafka.httpproxy.model.Consumer
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerName
import org.http4k.connect.kafka.httpproxy.model.JsonRecord
import org.http4k.connect.kafka.httpproxy.model.Offset
import org.http4k.connect.kafka.httpproxy.model.PartitionId
import org.http4k.connect.kafka.httpproxy.model.Record
import org.http4k.connect.kafka.httpproxy.model.RecordFormat
import org.http4k.connect.kafka.httpproxy.model.RecordFormat.avro
import org.http4k.connect.kafka.httpproxy.model.RecordFormat.binary
import org.http4k.connect.kafka.httpproxy.model.RecordFormat.json
import org.http4k.connect.kafka.httpproxy.model.Records
import org.http4k.connect.kafka.httpproxy.model.Records.Json
import org.http4k.connect.kafka.httpproxy.model.Topic
import org.http4k.connect.kafka.httpproxy.model.TopicRecord
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.successValue
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.junit.jupiter.api.Test
import java.util.UUID

@Suppress("UNCHECKED_CAST")
abstract class KafkaHttpProxyContract {

    abstract val http: HttpHandler
    abstract val uri: Uri

    private val kafkaHttpProxy by lazy {
        KafkaHttpProxy.Http(Credentials("", ""), uri, http)
    }

    @Test
    fun `can send JSON messages and get them back`() {
        kafkaHttpProxy.testSending(json, { (it as Json).records.first() as JsonRecord<String, Message> }) {
            Json(listOf(JsonRecord(it, Message(randomString()))))
        }
    }

    @Test
    fun `can send AVRO messages and get them back`() {
        kafkaHttpProxy.testSending(
            avro,
            { (it as Records.Avro).records.first() as AvroRecord<String, Message> }) {
            Records.Avro("schema", listOf(AvroRecord(it, Message(randomString()))))
        }
    }

    @Test
    fun `can send BINARY messages and get them back`() {
        kafkaHttpProxy.testSending(binary, { (it as Records.Binary).records.first() as BinaryRecord<String> }) {
            Records.Binary(listOf(BinaryRecord(it, Base64Blob.encode(randomString()))))
        }
    }

    @Test
    fun `can create consumer client`() {
        val topic1 = Topic.of("t1_${randomString()}")

        val group = ConsumerGroup.of(randomString())
        val name = ConsumerName.of(randomString())
        val consumer = KafkaHttpProxyConsumer.Http(
            Credentials("", ""), group,
            Consumer(name, json, "earliest", enableAutocommit = "true"), uri, http
        ).successValue()

        try {
            consumer.subscribeToTopics(listOf(topic1)).successValue()

            val record1 = Json(listOf(JsonRecord("m1", Message(randomString()))))
            kafkaHttpProxy.produceMessages(topic1, record1).successValue()

            assertThat(
                consumer.consumeRecords(json).successValue().toList(),
                equalTo(
                    listOf(
                        TopicRecord(
                            topic1, record1.records.first().key,
                            record1.records.first().value.toMap(), PartitionId.of(0), Offset.of(0)
                        )
                    )
                )
            )
        } finally {
            consumer.delete().successValue()
        }
    }

    @Test
    fun `can manually commit consumer offsets`() {
        val topic1 = Topic.of("t1_${randomString()}")

        val group = ConsumerGroup.of(randomString())
        val name = ConsumerName.of(randomString())
        val consumer = KafkaHttpProxyConsumer.Http(
            Credentials("", ""), group,
            Consumer(name, json, "earliest", enableAutocommit = "false"), uri, http
        ).successValue()

        try {
            consumer.subscribeToTopics(listOf(topic1)).successValue()

            val record1 = Json(listOf(JsonRecord("m1", Message(randomString()))))
            kafkaHttpProxy.produceMessages(topic1, record1).successValue()

            assertThat(consumer.consumeRecords(json).successValue().size, equalTo(1))
            assertThat(consumer.consumeRecords(json).successValue().size, equalTo(1))
            consumer.commitOffsets(
                listOf(CommitOffset(topic1, PartitionId.of(0), Offset.of(1)))
            ).successValue()
            assertThat(consumer.consumeRecords(json).successValue().size, equalTo(0))
        } finally {
            consumer.delete().successValue()
        }
    }

    private fun <K : Any, V : Any, T : Record<K, V>> KafkaHttpProxy.testSending(
        format: RecordFormat,
        recordFrom: (Records) -> T,
        buildRecords: (String) -> Records
    ) {
        val topic1 = Topic.of("t1_${randomString()}")
        val topic2 = Topic.of("t2_${randomString()}")
        val topic3 = Topic.of("t3_${randomString()}")

        val group = ConsumerGroup.of(randomString())
        val name = ConsumerName.of(randomString())

        val instance = createConsumer(group, Consumer(name, format, "earliest")).successValue().instance_id

        try {
            subscribeToTopics(group, instance, listOf(topic1, topic2)).successValue()

            val record1 = buildRecords("m1")
            val record2 = buildRecords("m2")
            val record3 = buildRecords("m3")
            val record4 = buildRecords("m4")
            val record5 = buildRecords("m5")

            produceMessages(topic1, record1).successValue()
            produceMessages(topic2, record2).successValue()
            produceMessages(topic3, record3).successValue()
            produceMessages(topic1, record4).successValue()

            assertThat(
                consumeRecords(group, instance, format).successValue().toList(),
                equalTo(
                    listOf(
                        TopicRecord(
                            topic1, recordFrom(record1).key,
                            recordFrom(record1).value.toMap(), PartitionId.of(0), Offset.of(0)
                        ),
                        TopicRecord(
                            topic2, recordFrom(record2).key,
                            recordFrom(record2).value.toMap(), PartitionId.of(0), Offset.of(0)
                        ),
                        TopicRecord(
                            topic1, recordFrom(record4).key,
                            recordFrom(record4).value.toMap(), PartitionId.of(0), Offset.of(1)
                        )
                    )
                )
            )

            assertThat(
                consumeRecords(group, instance, format).successValue().toList(),
                equalTo(emptyList())
            )

            produceMessages(topic1, record5).successValue()

            assertThat(
                consumeRecords(group, instance, format).successValue().toList(),
                equalTo(
                    listOf(
                        TopicRecord(
                            topic1, recordFrom(record5).key,
                            recordFrom(record5).value.toMap(), PartitionId.of(0), Offset.of(2)
                        ),
                    )
                )
            )
        } finally {
            deleteConsumer(group, instance)
        }
    }

    private fun randomString() = UUID.randomUUID().toString().take(5)
}

private fun <V : Any> V.toMap(): Any =
    KafkaHttpProxyMoshi.asA<Map<String, Any>>(asFormatString(this))

data class Message(val field: String)
