package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
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
    val DeleteRequest: Map<String, ItemAttributes>? = null,
    val PutRequest: Map<String, ItemAttributes>? = null
) {
    companion object {
        fun Delete(Key: ItemAttributes) = ReqWriteItem(DeleteRequest = mapOf("Key" to Key))
        fun Put(Item: ItemAttributes) = ReqWriteItem(PutRequest = mapOf("Item" to Item))
    }
}

@JsonSerializable
data class BatchWriteItems(
    val ConsumedCapacity: List<ConsumedCapacity>?,
    val Responses: Map<TableName, TokensToNames>?,
    val UnprocessedKeys: Map<String, ReqWriteItem>?
)
