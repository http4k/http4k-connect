package org.http4k.connect.amazon.dynamodb.mapper

data class DynamoDbPage<Document: Any, HashKey: Any, SortKey: Any>(
    val items: List<Document>,
    val nextHashKey: HashKey?,
    val nextSortKey: SortKey?
)
