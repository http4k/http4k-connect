package org.http4k.connect.amazon.model

import se.ansman.kotshi.JsonSerializable
import java.math.BigDecimal

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
            DynamoDataType.B -> Base64(Base64Blob.of(value as String))
            DynamoDataType.BOOL -> Bool(value.toString().toBoolean())
            DynamoDataType.BS -> Base64Set((value as List<String>).map(Base64Blob::of).toSet())
            DynamoDataType.L -> List((value as List<Map<String, Any>>).map { it.toAttributeValue() })
            DynamoDataType.M -> Map(
                (value as Map<String, Map<String, Any>>)
                    .map { AttributeName.of(it.key) to it.value.toAttributeValue() }.toMap()
            )
            DynamoDataType.N -> Num(BigDecimal(value as String))
            DynamoDataType.NS -> NumSet((value as List<String>).map(::BigDecimal).toSet())
            DynamoDataType.NULL -> Null()
            DynamoDataType.S -> Str(value as String)
            DynamoDataType.SS -> StrSet((value as List<String>).toSet())
        }

        private fun Map<String, Any>.toAttributeValue(): AttributeValue =
            entries.first().let { (k, v) -> from(DynamoDataType.valueOf(k), v) }
    }
}
