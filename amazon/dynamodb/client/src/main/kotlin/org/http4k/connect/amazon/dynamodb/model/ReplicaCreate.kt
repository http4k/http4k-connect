package org.http4k.connect.amazon.dynamodb.model

import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Region
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ReplicaCreate(
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexesUpdate>? = null,
    val KMSMasterKeyId: KMSKeyId? = null,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride? = null,
    val RegionName: Region? = null
)
