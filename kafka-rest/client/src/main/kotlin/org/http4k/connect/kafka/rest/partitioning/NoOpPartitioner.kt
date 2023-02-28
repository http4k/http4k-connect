package org.http4k.connect.kafka.rest.partitioning

import org.http4k.connect.kafka.rest.model.PartitionId

/**
 * Always selects no partition as a strategy
 */
fun <K : Any?, V : Any?> NoOpPartitioner(partitions: List<PartitionId>) = Partitioner { _: K, _: V ->
    null
}
