package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.regex
import org.http4k.connect.amazon.dynamodb.action.AttributeValue
import org.http4k.connect.amazon.dynamodb.action.KeySchema
import org.http4k.connect.amazon.dynamodb.action.NamesToValues
import org.http4k.connect.amazon.model.DynamoDataType.B
import org.http4k.connect.amazon.model.DynamoDataType.BOOL
import org.http4k.connect.amazon.model.DynamoDataType.BS
import org.http4k.connect.amazon.model.DynamoDataType.L
import org.http4k.connect.amazon.model.DynamoDataType.M
import org.http4k.connect.amazon.model.DynamoDataType.N
import org.http4k.connect.amazon.model.DynamoDataType.NS
import org.http4k.connect.amazon.model.DynamoDataType.S
import org.http4k.connect.amazon.model.DynamoDataType.SS
import se.ansman.kotshi.JsonSerializable
import java.math.BigDecimal

data class Attribute<IN, OUT>(
    val name: AttributeName,
    val type: DynamoDataType,
    private val toVal: (IN) -> AttributeValue,
    private val fromValue: (AttributeValue) -> OUT?
) {
    /**
     * Create a typed binding for this attribute
     */
    infix fun to(t: IN) = name to toVal(t)

    /**
     * Used for creating tables
     */
    fun keySchema(keyType: KeyType) = KeySchema(name, keyType)

    /**
     * Used for creating tables
     */
    fun attrDefinition() = AttributeDefinition(name, type)

    override fun toString() = name.toString()

    /**
     * Lookup this attribute from a queried Item
     */
    operator fun get(item: NamesToValues): OUT? = item[name]?.let { fromValue(it) }

    companion object {
        fun boolean(name: String) = Attribute(AttributeName.of(name), BOOL, AttributeValue::Bool, AttributeValue::BOOL)
        fun base64Blob(name: String) = Attribute(AttributeName.of(name), B, AttributeValue::Base64, AttributeValue::B)
        fun base64Blobs(name: String) =
            Attribute(AttributeName.of(name), BS, AttributeValue::Base64Set, AttributeValue::BS)
        fun list(name: String) = Attribute(AttributeName.of(name), L, AttributeValue::List, AttributeValue::L)
        fun map(name: String) = Attribute(AttributeName.of(name), M, AttributeValue::Map, AttributeValue::M)
        fun number(name: String) = Attribute(AttributeName.of(name), N, AttributeValue::Num) { BigDecimal(it.N) }
        fun numbers(name: String) =
            Attribute(AttributeName.of(name), NS, AttributeValue::NumSet) { it.NS?.map(::BigDecimal)?.toSet() }
        fun string(name: String) = Attribute(AttributeName.of(name), S, AttributeValue::Str, AttributeValue::S)
        fun strings(name: String) = Attribute(AttributeName.of(name), SS, AttributeValue::StrSet, AttributeValue::SS)
    }
}

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
