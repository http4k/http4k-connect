package org.http4k.connect.kafka.rest.partitioning

import org.http4k.connect.kafka.rest.model.PartitionId

/**
 * Uses the key hash to get the partition
 */
fun <K, V> StickyKeyRecordPartitioner(partitions: List<PartitionId>) =
    Partitioner<K, V> { p1, _ ->
        partitions[(p1).hashCode() % partitions.size] }
