package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Attribute
import org.http4k.connect.amazon.model.AttributeName
import org.http4k.connect.amazon.model.AttributeValue
import org.http4k.connect.amazon.model.DynamoDataType
import org.http4k.connect.amazon.model.DynamoDataType.valueOf
import org.http4k.connect.amazon.model.IndexName
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Key
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.amazon.model.Timestamp
import se.ansman.kotshi.JsonSerializable

typealias TokensToNames = Map<String, AttributeName>
typealias TokensToValues = Map<String, AttributeValue>
typealias ItemResult = Map<String, Map<String, Any>>

@JsonSerializable
data class ItemCollectionMetrics(
    val ItemCollectionKey: Key? = null,
    val SizeEstimateRangeGB: List<Long>? = null)

@JsonSerializable
data class Capacity(
    val CapacityUnits: Long? = null,
    val ReadCapacityUnits: Long? = null,
    val WriteCapacityUnits: Long? = null)

@JsonSerializable
data class ConsumedCapacity(
    val TableName: TableName? = null,
    val CapacityUnits: Long? = null,
    val GlobalSecondaryIndexes: Map<String, Capacity>,
    val LocalSecondaryIndexes: Map<String, Capacity>? = null,
    val ReadCapacityUnits: Long? = null,
    val Table: Capacity? = null,
    val WriteCapacityUnits: Long? = null)


@JsonSerializable
data class ModifiedItem(
    val Attributes: ItemResult? = null,
    val ConsumedCapacity: ConsumedCapacity? = null,
    val ItemCollectionMetrics: ItemCollectionMetrics? = null)

fun ItemResult.toItem() =
    map {
        val (key, v) = it.value.entries.first()
        AttributeName.of(it.key) to AttributeValue.from(valueOf(key), v)
    }
        .toMap()


@JsonSerializable
data class KeySchema(
    val AttributeName: AttributeName,
    val KeyType: KeyType
)

@JsonSerializable
data class Projection(
    val NonKeyAttributes: List<AttributeName>? = null,
    val ProjectionType: ProjectionType? = null
)

@JsonSerializable
data class ProvisionedThroughput(
    val ReadCapacityUnits: Long,
    val WriteCapacityUnits: Long
)

@JsonSerializable
data class GlobalSecondaryIndex(
    val IndexName: IndexName,
    val KeySchema: List<KeySchema>,
    val Projection: Projection,
    val ProvisionedThroughput: ProvisionedThroughput? = null)

@JsonSerializable
data class LocalSecondaryIndexes(
    val IndexName: IndexName,
    val KeySchema: List<KeySchema>,
    val Projection: Projection
)

@JsonSerializable
data class SSESpecification(
    val Enabled: Boolean,
    val KMSMasterKeyId: KMSKeyId? = null,
    val SSEType: SSEType? = null
)

@JsonSerializable
data class StreamSpecification(
    val StreamEnabled: Boolean,
    val StreamViewType: StreamViewType? = null
)

@JsonSerializable
data class ArchivalSummary(
    val ArchivalBackupArn: ARN? = null,
    val ArchivalDateTime: Timestamp? = null,
    val ArchivalReason: String? = null)

@JsonSerializable
data class BillingModeSummary(
    val BillingMode: BillingMode? = null,
    val LastUpdateToPayPerRequestDateTime: Timestamp? = null)

@JsonSerializable
data class ProvisionedThroughputResponse(
    val LastDecreaseDateTime: Timestamp? = null,
    val LastIncreaseDateTime: Timestamp? = null,
    val NumberOfDecreasesToday: Long? = null,
    val ReadCapacityUnits: Long? = null,
    val WriteCapacityUnits: Long? = null)

@JsonSerializable
data class GlobalSecondaryIndexResponse(
    val Backfilling: Boolean? = null,
    val IndexArn: ARN? = null,
    val IndexName: String? = null,
    val IndexSizeBytes: Long? = null,
    val IndexStatus: IndexStatus? = null,
    val ItemCount: Long? = null,
    val KeySchema: List<KeySchema>? = null,
    val Projection: Projection? = null,
    val ProvisionedThroughput: ProvisionedThroughputResponse? = null
)

@JsonSerializable
data class LocalSecondaryIndexResponse(
    val IndexArn: ARN? = null,
    val IndexName: String? = null,
    val IndexSizeBytes: Long? = null,
    val ItemCount: Long? = null,
    val KeySchema: List<KeySchema>? = null,
    val Projection: Projection? = null)

@JsonSerializable
data class ProvisionedThroughputOverride(
    val ReadCapacityUnits: Long? = null)

@JsonSerializable
data class GlobalSecondaryIndexReplica(
    val IndexName: IndexName? = null,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride? = null)

@JsonSerializable
data class Replica(
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexReplica>? = null,
    val KMSMasterKeyId: KMSKeyId? = null,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride? = null,
    val RegionName: String? = null,
    val ReplicaInaccessibleDateTime: Timestamp? = null,
    val ReplicaStatus: ReplicaStatus? = null,
    val ReplicaStatusDescription: String? = null,
    val ReplicaStatusPercentProgress: String? = null)

@JsonSerializable
data class RestoreSummary(
    val RestoreDateTime: Timestamp? = null,
    val RestoreInProgress: Boolean? = null,
    val SourceBackupArn: ARN? = null,
    val SourceTableArn: ARN? = null)

@JsonSerializable
data class SSEDescription(
    val InaccessibleEncryptionDateTime: Timestamp? = null,
    val KMSMasterKeyArn: ARN? = null,
    val SSEType: SSEType? = null,
    val Status: String? = null)

@JsonSerializable
data class TableDescription(
    val ArchivalSummary: ArchivalSummary? = null,
    val AttributeDefinitions: List<AttributeDefinition>? = null,
    val BillingModeSummary: BillingModeSummary? = null,
    val CreationDateTime: Timestamp? = null,
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexResponse>? = null,
    val GlobalTableVersion: String? = null,
    val ItemCount: Long? = null,
    val KeySchema: List<KeySchema>? = null,
    val LatestStreamArn: ARN? = null,
    val LatestStreamLabel: String? = null,
    val LocalSecondaryIndexes: List<LocalSecondaryIndexResponse>? = null,
    val ProvisionedThroughput: ProvisionedThroughputResponse? = null,
    val Replicas: List<Replica>? = null,
    val RestoreSummary: RestoreSummary? = null,
    val SSEDescription: SSEDescription? = null,
    val StreamSpecification: StreamSpecification? = null,
    val TableArn: ARN? = null,
    val TableId: String? = null,
    val TableName: TableName? = null,
    val TableSizeBytes: Long? = null,
    val TableStatus: TableStatus? = null)

@JsonSerializable
data class TableDescriptionResponse(
    val TableDescription: TableDescription
)

@JsonSerializable
data class GlobalSecondaryIndexCreate(
    val IndexName: IndexName? = null,
    val KeySchema: List<KeySchema>? = null,
    val Projection: Projection? = null,
    val ProvisionedThroughput: ProvisionedThroughput? = null)

@JsonSerializable
data class GlobalSecondaryIndexDelete(
    val IndexName: IndexName? = null)

@JsonSerializable
data class GlobalSecondaryIndexUpdate(
    val IndexName: IndexName? = null,
    val ProvisionedThroughput: ProvisionedThroughput? = null)

@JsonSerializable
data class GlobalSecondaryIndexUpdates(
    val Create: GlobalSecondaryIndexCreate? = null,
    val Delete: GlobalSecondaryIndexDelete? = null,
    val Update: GlobalSecondaryIndexUpdate? = null)

@JsonSerializable
data class GlobalSecondaryIndexesUpdate(
    val IndexName: IndexName? = null,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride? = null)

@JsonSerializable
data class ReplicaCreate(
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexesUpdate>? = null,
    val KMSMasterKeyId: KMSKeyId? = null,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride? = null,
    val RegionName: Region? = null)

@JsonSerializable
data class ReplicaDelete(val RegionName: Region?)

@JsonSerializable
data class ReplicaUpdate(
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexesUpdate>? = null,
    val KMSMasterKeyId: KMSKeyId? = null,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride? = null,
    val RegionName: String? = null)

@JsonSerializable
data class ReplicaUpdates(
    val Create: ReplicaCreate? = null,
    val Delete: ReplicaDelete? = null,
    val Update: ReplicaUpdate? = null)

@JsonSerializable
data class AttributeDefinition(
    val AttributeName: AttributeName,
    val AttributeType: DynamoDataType
)

enum class BillingMode {
    PROVISIONED, PAY_PER_REQUEST
}

enum class KeyType {
    HASH, RANGE
}

enum class ProjectionType {
    ALL, KEYS_ONLY, INCLUDE
}

enum class SSEType {
    AES256, KMS
}

enum class StreamViewType {
    NEW_IMAGE, OLD_IMAGE, NEW_AND_OLD_IMAGES, KEYS_ONLY
}

enum class IndexStatus {
    CREATING, UPDATING, DELETING, ACTIVE
}

enum class ReplicaStatus {
    CREATING, CREATION_FAILED, UPDATING, DELETING, ACTIVE, REGION_DISABLED, INACCESSIBLE_ENCRYPTION_CREDENTIALS
}

enum class TableStatus {
    CREATING, UPDATING, DELETING, ACTIVE, INACCESSIBLE_ENCRYPTION_CREDENTIALS, ARCHIVING, ARCHIVED
}

enum class ReturnConsumedCapacity {
    INDEXES, TOTAL, NONE
}

enum class ReturnItemCollectionMetrics {
    SIZE, NONE
}

enum class ReturnValues {
    NONE, ALL_OLD, UPDATED_OLD, ALL_NEW, UPDATED_NEW
}

/**
 * Used for creating tables
 */
fun <T> Attribute<T>.asKeySchema(keyType: KeyType) = KeySchema(name, keyType)

/**
 * Used for creating tables
 */
fun <T> Attribute<T>.asAttributeDefinition() = AttributeDefinition(name, dataType)

