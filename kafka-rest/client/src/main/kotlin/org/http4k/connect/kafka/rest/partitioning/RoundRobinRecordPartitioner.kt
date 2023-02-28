package org.http4k.connect.kafka.rest.partitioning

import org.http4k.connect.kafka.rest.model.PartitionId

/**
 * Round robins from the list of partitions
 */
fun <K : Any?, V : Any?> RoundRobinRecordPartitioner(partitions: List<PartitionId>): Partitioner<K, V> {
    var index = 0

    return Partitioner { _, _ ->
        if (index >= partitions.size) index = 0
        partitions[index++]
    }
}
