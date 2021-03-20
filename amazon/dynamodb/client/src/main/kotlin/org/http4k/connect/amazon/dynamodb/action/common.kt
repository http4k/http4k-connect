package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.amazon.model.AttributeName
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

typealias AttributeNames = Map<String, AttributeName>
typealias AttributeValues = Map<String, AttributeValue>
typealias ItemResult = Map<String, Map<String, Any>>

@JsonSerializable
data class AttributeValue(
    val B: Base64Blob? = null,
    val BOOL: Boolean? = null,
    val BS: List<Base64Blob>? = null,
    val L: List<AttributeValue>? = null,
    val M: AttributeValues? = null,
    val N: String? = null,
    val NS: List<String>? = null,
    val NULL: Boolean? = null,
    val S: String? = null,
    val SS: List<String>? = null
) {
    companion object {
        fun Base64(value: Base64Blob) = AttributeValue(B = value)
        fun Bool(value: Boolean) = AttributeValue(BOOL = value)
        fun Base64List(value: List<Base64Blob>) = AttributeValue(BS = value)
        fun List(value: List<AttributeValue>) = AttributeValue(L = value)
        fun Map(value: AttributeValues) = AttributeValue(M = value)
        fun Num(value: Number) = AttributeValue(N = value.toString())
        fun NumList(value: List<Number>) = AttributeValue(NS = value.map { it.toString() })
        fun Null() = AttributeValue(NULL = true)
        fun Str(value: String) = AttributeValue(S = value)
        fun StrList(value: List<String>) = AttributeValue(SS = value)

        @Suppress("UNCHECKED_CAST")
        fun from(entry: Map.Entry<String, Any>): AttributeValue = when (entry.key) {
            "B" -> Base64(Base64Blob.encoded(entry.value.toString()))
            "BOOL" -> Bool(entry.value.toString().toBoolean())
            "BS" -> Base64List((entry.value as List<String>).map { Base64Blob.encoded(entry.value.toString()) })
            "L" -> List((entry.value as List<Map<String, Any>>).map { from(it.entries.first()) })
            "N" -> Num(entry.value.toString().toLong())
            "NS" -> NumList((entry.value as List<String>).map { entry.value.toString().toLong() })
            "NULL" -> Null()
            "S" -> Str(entry.value.toString())
            "SS" -> StrList(entry.value as List<String>)
            else -> error("illegal response")
        }
    }
}

@JsonSerializable
data class ItemCollectionMetrics(
    val ItemCollectionKey: AttributeValues?,
    val SizeEstimateRangeGB: List<Long>?
)

@JsonSerializable
data class Capacity(
    val CapacityUnits: Long?,
    val ReadCapacityUnits: Long?,
    val WriteCapacityUnits: Long?
)

@JsonSerializable
data class ConsumedCapacity(
    val TableName: TableName?,
    val CapacityUnits: Long?,
    val GlobalSecondaryIndexes: Map<String, Capacity>,
    val LocalSecondaryIndexes: Map<String, Capacity>?,
    val ReadCapacityUnits: Long?,
    val Table: Capacity?,
    val WriteCapacityUnits: Long?
)

enum class ReturnConsumedCapacity {
    INDEXES, TOTAL, NONE
}

enum class ReturnItemCollectionMetrics {
    SIZE, NONE
}

enum class ReturnValues {
    NONE, ALL_OLD, UPDATED_OLD, ALL_NEW, UPDATED_NEW
}

@JsonSerializable
data class ModifiedItem(
    val Attributes: ItemResult?,
    val ConsumedCapacity: ConsumedCapacity?,
    val ItemCollectionMetrics: ItemCollectionMetrics?
)

fun ItemResult.toItem() =
    map {
        AttributeName.of(it.key) to
            AttributeValue.from(it.value.entries.first())
    }
        .toMap()

