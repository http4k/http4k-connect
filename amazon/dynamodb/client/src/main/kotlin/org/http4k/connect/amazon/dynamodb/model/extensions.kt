package org.http4k.connect.amazon.dynamodb.model

import org.http4k.connect.amazon.dynamodb.model.DynamoDataType.valueOf
import org.http4k.lens.BiDiMapping

typealias TokensToNames = Map<String, AttributeName>
typealias TokensToValues = Map<String, AttributeValue>
typealias ItemResult = Map<String, Map<String, Any>>

fun ItemResult.toItem() =
    map {
        val (key, v) = it.value.entries.first()
        AttributeName.of(it.key) to AttributeValue.from(valueOf(key), v)
    }
        .toMap()

/**
 * Used for creating tables
 */
fun <T> Attribute<T>.asKeySchema(keyType: KeyType) = KeySchema(name, keyType)

/**
 * Used for creating tables
 */
fun <T> Attribute<T>.asAttributeDefinition() = AttributeDefinition(name, dataType)

/**
 * Map items out of a collection
 */
fun <T> Attribute.AttrLensSpec<List<AttributeValue>>.map(next: BiDiMapping<Item, T>): Attribute.AttrLensSpec<List<T>> = map(
    { it.mapNotNull { it.M }.map { next(it) } },
    { it.map { AttributeValue.Map(next(it)) } },
)

/**
 * Map items out of a collection
 */
fun <T> Attribute.Companion.list(next: BiDiMapping<Item, T>) = list().map(
    { it.mapNotNull { it.M }.map { next(it) } },
    { it.map { AttributeValue.Map(next(it)) } },
)

/**
 * Map items out of a collection
 */
fun <T> Attribute.Companion.map(next: BiDiMapping<Item, T>) = map().map(next)

