package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import org.http4k.connect.amazon.model.KeyType.HASH
import org.http4k.connect.amazon.model.KeyType.RANGE
import se.ansman.kotshi.JsonSerializable

class AttributeName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<AttributeName>(::AttributeName)
}

enum class AttributeType {
    S, N, B
}

enum class BillingMode {
    PROVISIONED, PAY_PER_REQUEST
}

class IndexName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<IndexName>(::IndexName)
}

class TableName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<TableName>(::TableName)
}

@JsonSerializable
data class AttributeDefinition(
    val AttributeName: AttributeName,
    val AttributeType: AttributeType
)

fun AttributeName.booleanAttrDefinition() = AttributeDefinition(this, AttributeType.B)
fun AttributeName.stringAttrDefinition() = AttributeDefinition(this, AttributeType.S)
fun AttributeName.numberAttrDefinition() = AttributeDefinition(this, AttributeType.N)

fun AttributeName.hashKeySchema() = KeySchema(this, HASH)
fun AttributeName.rangeKeySchema() = KeySchema(this, RANGE)

enum class KeyType {
    HASH, RANGE
}

@JsonSerializable
data class KeySchema(
    val AttributeName: AttributeName,
    val KeyType: KeyType
)

enum class ProjectionType {
    ALL, KEYS_ONLY, INCLUDE
}

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
    val ProvisionedThroughput: ProvisionedThroughput?
)

@JsonSerializable
data class LocalSecondaryIndexes(
    val IndexName: IndexName,
    val KeySchema: List<KeySchema>,
    val Projection: Projection
)

enum class SSEType {
    AES256, KMS
}

@JsonSerializable
data class SSESpecification(
    val Enabled: Boolean,
    val KMSMasterKeyId: KMSKeyId? = null,
    val SSEType: SSEType? = null
)

enum class StreamViewType {
    NEW_IMAGE, OLD_IMAGE, NEW_AND_OLD_IMAGES, KEYS_ONLY
}

@JsonSerializable
data class StreamSpecification(
    val StreamEnabled: Boolean,
    val StreamViewType: StreamViewType? = null
)

@JsonSerializable
data class ArchivalSummary(
    val ArchivalBackupArn: ARN?,
    val ArchivalDateTime: Timestamp?,
    val ArchivalReason: String?
)

@JsonSerializable
data class BillingModeSummary(
    val BillingMode: String?,
    val LastUpdateToPayPerRequestDateTime: Timestamp?
)

@JsonSerializable
data class ProvisionedThroughputResponse(
    val LastDecreaseDateTime: Timestamp?,
    val LastIncreaseDateTime: Timestamp?,
    val NumberOfDecreasesToday: Long?,
    val ReadCapacityUnits: Long?,
    val WriteCapacityUnits: Long?
)

enum class IndexStatus {
    CREATING, UPDATING, DELETING, ACTIVE
}

@JsonSerializable
data class GlobalSecondaryIndexResponse(
    val Backfilling: Boolean?,
    val IndexArn: ARN?,
    val IndexName: String?,
    val IndexSizeBytes: Long?,
    val IndexStatus: IndexStatus?,
    val ItemCount: Long?,
    val KeySchema: List<KeySchema>?,
    val Projection: Projection?,
    val ProvisionedThroughput: ProvisionedThroughputResponse?

)

@JsonSerializable
data class LocalSecondaryIndexResponse(
    val IndexArn: ARN?,
    val IndexName: String?,
    val IndexSizeBytes: Long?,
    val ItemCount: Long?,
    val KeySchema: List<KeySchema>?,
    val Projection: Projection?
)

@JsonSerializable
data class ProvisionedThroughputOverride(
    val ReadCapacityUnits: Long?
)

@JsonSerializable
data class GlobalSecondaryIndexReplica(
    val IndexName: IndexName?,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride?
)

enum class ReplicaStatus {
    CREATING, CREATION_FAILED, UPDATING, DELETING, ACTIVE, REGION_DISABLED, INACCESSIBLE_ENCRYPTION_CREDENTIALS
}

@JsonSerializable
data class Replica(
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexReplica>?,
    val KMSMasterKeyId: KMSKeyId?,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride?,
    val RegionName: String?,
    val ReplicaInaccessibleDateTime: Timestamp?,
    val ReplicaStatus: ReplicaStatus?,
    val ReplicaStatusDescription: String?,
    val ReplicaStatusPercentProgress: String?
)

@JsonSerializable
data class RestoreSummary(
    val RestoreDateTime: Timestamp?,
    val RestoreInProgress: Boolean?,
    val SourceBackupArn: ARN?,
    val SourceTableArn: ARN?
)

@JsonSerializable
data class SSEDescription(
    val InaccessibleEncryptionDateTime: Timestamp?,
    val KMSMasterKeyArn: ARN?,
    val SSEType: SSEType?,
    val Status: String?
)

enum class TableStatus {
    CREATING, UPDATING, DELETING, ACTIVE, INACCESSIBLE_ENCRYPTION_CREDENTIALS, ARCHIVING, ARCHIVED
}

@JsonSerializable
data class TableDescription(
    val ArchivalSummary: ArchivalSummary?,
    val AttributeDefinitions: List<AttributeDefinition>?,
    val BillingModeSummary: BillingModeSummary?,
    val CreationDateTime: Timestamp?,
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexResponse>?,
    val GlobalTableVersion: String?,
    val ItemCount: Long?,
    val KeySchema: List<KeySchema>?,
    val LatestStreamArn: ARN?,
    val LatestStreamLabel: String?,
    val LocalSecondaryIndexes: List<LocalSecondaryIndexResponse>?,
    val ProvisionedThroughput: ProvisionedThroughputResponse?,
    val Replicas: List<Replica>?,
    val RestoreSummary: RestoreSummary?,
    val SSEDescription: SSEDescription?,
    val StreamSpecification: StreamSpecification?,
    val TableArn: ARN?,
    val TableId: String?,
    val TableName: TableName?,
    val TableSizeBytes: Long?,
    val TableStatus: TableStatus?
)

@JsonSerializable
data class TableDescriptionResponse(
    val TableDescription: TableDescription
)

@JsonSerializable
data class GlobalSecondaryIndexCreate(
    val IndexName: IndexName?,
    val KeySchema: List<KeySchema>?,
    val Projection: Projection?,
    val ProvisionedThroughput: ProvisionedThroughput?
)

@JsonSerializable
data class GlobalSecondaryIndexDelete(
    val IndexName: IndexName?
)

@JsonSerializable
data class GlobalSecondaryIndexUpdate(
    val IndexName: IndexName?,
    val ProvisionedThroughput: ProvisionedThroughput?
)

@JsonSerializable
data class GlobalSecondaryIndexUpdates(
    val Create: GlobalSecondaryIndexCreate?,
    val Delete: GlobalSecondaryIndexDelete?,
    val Update: GlobalSecondaryIndexUpdate?
)

@JsonSerializable
data class GlobalSecondaryIndexesUpdate(
    val IndexName: IndexName?,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride?
)

@JsonSerializable
data class ReplicaCreate(
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexesUpdate>?,
    val KMSMasterKeyId: KMSKeyId?,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride?,
    val RegionName: Region?
)

@JsonSerializable
data class ReplicaDelete(val RegionName: Region?)

@JsonSerializable
data class ReplicaUpdate(
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexesUpdate>?,
    val KMSMasterKeyId: KMSKeyId?,
    val ProvisionedThroughputOverride: ProvisionedThroughputOverride?,
    val RegionName: String?
)

@JsonSerializable
data class ReplicaUpdates(
    val Create: ReplicaCreate?,
    val Delete: ReplicaDelete?,
    val Update: ReplicaUpdate?
)
