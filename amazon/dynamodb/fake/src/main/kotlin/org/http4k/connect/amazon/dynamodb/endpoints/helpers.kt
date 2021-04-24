package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.TableDefinition
import org.http4k.connect.amazon.dynamodb.action.GetResponse
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.storage.Storage

fun Item.asItemResult(): Map<String, Map<String, Any>> =
    mapKeys {
        it.key.value
    }.mapValues {
        DynamoDbMoshi.asA(DynamoDbMoshi.asFormatString(it.value))
    }

fun Storage<TableDefinition>.getItemByKey(tableName: TableName, key: Key): GetResponse? =
    this[tableName.value]?.let { GetResponse(it.retrieve(key)?.asItemResult()) }
