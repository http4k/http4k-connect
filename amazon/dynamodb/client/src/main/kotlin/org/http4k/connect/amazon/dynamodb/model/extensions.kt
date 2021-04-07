package org.http4k.connect.amazon.dynamodb.model

import org.http4k.connect.amazon.dynamodb.model.DynamoDataType.valueOf

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

