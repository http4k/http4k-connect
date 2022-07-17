package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens

inline fun <reified Document: Any, HashKey: Any, SortKey: Any> DynamoDb.moshiTableMapper(
    TableName: TableName,
    hashKeyAttribute: Attribute<HashKey>,
    sortKeyAttribute: Attribute<SortKey>? = null
) = moshiTableMapper<Document, HashKey, SortKey>(
    TableName = TableName,
    primarySchema = DynamoDbTableMapperSchema.Primary(hashKeyAttribute, sortKeyAttribute)
)

inline fun <reified Document: Any, HashKey: Any, SortKey: Any> DynamoDb.moshiTableMapper(
    TableName: TableName,
    primarySchema: DynamoDbTableMapperSchema.Primary<HashKey, SortKey>
): DynamoDbTableMapper<Document, HashKey, SortKey> {
    return DynamoDbTableMapper(
        dynamoDb = this,
        tableName = TableName,
        itemLens = DynamoDbMoshi.autoDynamoLens(),
        primarySchema = primarySchema
    )
}
