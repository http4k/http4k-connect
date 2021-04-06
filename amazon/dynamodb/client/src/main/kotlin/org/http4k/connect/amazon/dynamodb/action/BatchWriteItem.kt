package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.Item
import org.http4k.connect.amazon.model.Key
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class BatchWriteItem(
    val RequestItems: Map<TableName, List<ReqWriteItem>>,
    val ReturnItemCollectionMetrics: ReturnItemCollectionMetrics? = null,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null
) : DynamoDbAction<BatchWriteItems>(BatchWriteItems::class, DynamoDbMoshi)

@JsonSerializable
data class ReqWriteItem internal constructor(
    val DeleteRequest: Map<String, Item>? = null,
    val PutRequest: Map<String, Item>? = null
) {
    companion object {
        fun Delete(Key: Key) = ReqWriteItem(DeleteRequest = mapOf("Key" to Key))
        fun Put(Item: Item) = ReqWriteItem(PutRequest = mapOf("Item" to Item))
    }
}

@JsonSerializable
data class BatchWriteItems(
    val ConsumedCapacity: List<ConsumedCapacity>? = null,
    val Responses: Map<TableName, TokensToNames>? = null,
    val UnprocessedKeys: Map<String, ReqWriteItem>? = null)
