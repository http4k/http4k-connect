package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class BatchWriteItem(
    val RequestItems: Map<TableName, ReqWriteItem>,
    val ReturnItemCollectionMetrics: ReturnItemCollectionMetrics? = null,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null
) : DynamoDbAction<BatchItems>(BatchItems::class, DynamoDbMoshi)

@JsonSerializable
data class ReqWriteItem(
    val DeleteRequest: Map<String, AttributeValue>?,
    val PutRequest: PutRequest?
)

@JsonSerializable
data class PutRequest(val Item: AttributeValues)
