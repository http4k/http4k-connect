package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.model.Item

fun Item.asItemResult(): Map<String, Map<String, Any>> =
    mapKeys {
        it.key.value
    }.mapValues {
        DynamoDbMoshi.asA(DynamoDbMoshi.asFormatString(it.value))
    }
