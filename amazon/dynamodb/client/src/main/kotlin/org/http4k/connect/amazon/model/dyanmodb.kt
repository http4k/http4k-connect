package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.regex
import org.http4k.connect.amazon.dynamodb.action.Item
import org.http4k.lens.BiDiLens
import se.ansman.kotshi.JsonSerializable


val <FINAL> BiDiLens<Item, FINAL>.name get() = AttributeName.of(meta.name)

class AttributeName private constructor(value: String) : StringValue(value), Comparable<AttributeName> {
    companion object : NonBlankStringValueFactory<AttributeName>(::AttributeName)

    override fun compareTo(other: AttributeName): Int = value.compareTo(other.value)
}

enum class DynamoDataType {
    B, BOOL, BS, L, M, N, NS, NULL, S, SS
}

enum class BillingMode {
    PROVISIONED, PAY_PER_REQUEST
}

class IndexName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<IndexName>(::IndexName)
}

class TableName private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TableName>(::TableName, "[a-zA-Z0-9_.-]+".regex)
}

@JsonSerializable
data class AttributeDefinition(
    val AttributeName: AttributeName,
    val AttributeType: DynamoDataType
)

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
