package org.http4k.connect.kafka.rest.action

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.kClass
import org.http4k.connect.kafka.rest.KafkaRest
import org.http4k.connect.kafka.rest.KafkaRestMoshi.asFormatString
import org.http4k.connect.kafka.rest.getPartitions
import org.http4k.connect.kafka.rest.model.PartitionId
import org.http4k.connect.kafka.rest.model.PartitionOffset
import org.http4k.connect.kafka.rest.model.Records
import org.http4k.connect.kafka.rest.model.SchemaId
import org.http4k.connect.kafka.rest.model.Topic
import org.http4k.connect.kafka.rest.partitioning.Partitioner
import org.http4k.connect.kafka.rest.produceMessages
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class ProduceMessages(val topic: Topic, val records: Records) :
    KafkaRestAction<ProducedMessages>(kClass()) {

    override fun toRequest() = Request(POST, "/topics/$topic")
        .header("Accept", "application/vnd.kafka.v2+json")
        .with(CONTENT_TYPE of records.contentType)
        .body(asFormatString(records))
}

@JsonSerializable
data class ProducedMessages(
    val key_schema_id: SchemaId?,
    val value_schema_id: SchemaId?,
    val offsets: List<PartitionOffset>
)

/**
 * Rewrites the partitions of messages using the passed Partitioner, after getting the list of partitions to write to
 */
fun KafkaRest.produceMessages(
    topic: Topic,
    records: Records,
    fn: (List<PartitionId>) -> Partitioner<Any?, Any?>
): Result<ProducedMessages, RemoteFailure> = getPartitions(topic)
    .map {
        val partitioner = fn(it.map(Partition::partition).toList())
        records.copy(records = records.records
            .map { it.copy(partition = partitioner(it.key, it.value)) })
    }
    .flatMap { produceMessages(topic, it) }
