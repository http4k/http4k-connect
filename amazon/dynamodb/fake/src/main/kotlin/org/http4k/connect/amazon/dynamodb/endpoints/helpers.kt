package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.GetResponse
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.storage.Storage

fun Item.asItemResult(): Map<String, Map<String, Any>> =
    mapKeys { it.key.value }.mapValues { convert(it.value) }

fun Storage<DynamoTable>.getItemByKey(tableName: TableName, key: Key): GetResponse? =
    this[tableName.value]?.let { GetResponse(it.retrieve(key)?.asItemResult()) }

fun Storage<DynamoTable>.putItem(tableName: TableName, item: Item) {
    this[tableName.value]?.let {
        this[tableName.value] = it.withItem(item)
        ModifiedItem(item.asItemResult())
    }
}

inline fun <reified OUT : Any> convert(input: Any) = DynamoDbMoshi.asA<OUT>(DynamoDbMoshi.asFormatString(input))
