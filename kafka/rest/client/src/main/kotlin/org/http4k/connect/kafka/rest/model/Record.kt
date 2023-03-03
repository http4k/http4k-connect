package org.http4k.connect.kafka.rest.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Record<K : Any, out V : Any>(val key: K?, val `value`: V, val partition: PartitionId? = null)
