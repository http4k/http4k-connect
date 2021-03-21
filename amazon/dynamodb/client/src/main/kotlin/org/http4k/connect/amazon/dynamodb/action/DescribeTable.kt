package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class DescribeTable(val TableName: TableName) :
    DynamoDbAction<DescribedTable>(DescribedTable::class, DynamoDbMoshi)

@JsonSerializable
data class DescribedTable(
    val Table: TableDescription
)
