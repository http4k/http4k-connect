package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.B
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.BOOL
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.BS
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.L
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.M
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.N
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.NS
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.NULL
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.S
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.SS
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.valueOf
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AttributeName
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.IndexName
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.amazon.model.Timestamp
import se.ansman.kotshi.JsonSerializable
import java.math.BigDecimal

typealias TokensToNames = Map<String, AttributeName>
typealias TokensToValues = Map<String, AttributeValue>
typealias ItemResult = Map<String, Map<String, Any>>

/**
 * Represents the on-the-wire format of an Attribute Value with it's requisite type.
 * Only one of these fields is ever populated at once in an entry. So you can get
 * { "S": "hello" } or { "BOOL": true } or { "NS": ["123"] }
 */
@JsonSerializable
data class AttributeValue internal constructor(
    val B: Base64Blob? = null,
    val BOOL: Boolean? = null,
    val BS: Set<Base64Blob>? = null,
    val L: List<AttributeValue>? = null,
    val M: Item? = null,
    val N: String? = null,
    val NS: Set<String>? = null,
    val NULL: Boolean? = null,
    val S: String? = null,
    val SS: Set<String>? = null
) {
    companion object {
        fun Base64(value: Base64Blob?) = value?.let { AttributeValue(B = it) } ?: Null()
        fun Bool(value: Boolean?) = value?.let { AttributeValue(BOOL = it) } ?: Null()
        fun Base64Set(value: Set<Base64Blob>?) = value?.let { AttributeValue(BS = it) } ?: Null()
        fun List(value: List<AttributeValue>?) = value?.let { AttributeValue(L = it) } ?: Null()
        fun Map(value: Item?) = value?.let { AttributeValue(M = it) } ?: Null()
        fun Num(value: Number?) = value?.let { AttributeValue(N = it.toString()) } ?: Null()
        fun NumSet(value: Set<Number>?) = value?.let { AttributeValue(NS = it.map { it.toString() }.toSet()) } ?: Null()
        fun Null() = AttributeValue(NULL = true)
        fun Str(value: String?) = value?.let { AttributeValue(S = it) } ?: Null()
        fun StrSet(value: Set<String>?) = value?.let { AttributeValue(SS = it.map { it }.toSet()) } ?: Null()

        @Suppress("UNCHECKED_CAST")
        fun from(key: DynamoDataType, value: Any): AttributeValue = when (key) {
            B -> Base64(Base64Blob.of(value as String))
            BOOL -> Bool(value.toString().toBoolean())
            BS -> Base64Set((value as List<String>).map(Base64Blob::of).toSet())
            L -> List((value as List<Map<String, Any>>).map { it.toAttributeValue() })
            M -> Map(
                (value as Map<String, Map<String, Any>>)
                    .map { AttributeName.of(it.key) to it.value.toAttributeValue() }.toMap()
            )
            N -> Num(BigDecimal(value as String))
            NS -> NumSet((value as List<String>).map(::BigDecimal).toSet())
            NULL -> Null()
            S -> Str(value as String)
            SS -> StrSet((value as List<String>).toSet())
        }

        private fun Map<String, Any>.toAttributeValue(): AttributeValue =
            entries.first().let { (k, v) -> from(valueOf(k), v) }
    }
}

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

enum class DynamoDataType {
    B, BOOL, BS, L, M, N, NS, NULL, S, SS
}

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
