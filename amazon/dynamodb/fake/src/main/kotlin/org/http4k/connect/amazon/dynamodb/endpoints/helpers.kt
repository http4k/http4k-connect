package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi.asA
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi.asFormatString
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.GetResponse
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.storage.Storage

fun Item.asItemResult(): Map<String, Map<String, Any>> =
    mapKeys {
        it.key.value
    }.mapValues {
        asA(asFormatString(it.value))
    }

fun Storage<DynamoTable>.getItemByKey(tableName: TableName, key: Key): GetResponse? =
    this[tableName.value]?.let { GetResponse(it.retrieve(key)?.asItemResult()) }

fun Storage<DynamoTable>.putItem(tableName: TableName, item: Item) {
    this[tableName.value]?.let {
        this[tableName.value] = it.withItem(item)
        ModifiedItem(item.asItemResult())
    }
}
