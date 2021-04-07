package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.model.ConsumedCapacity
import org.http4k.connect.amazon.dynamodb.model.ReqWriteItem
import org.http4k.connect.amazon.dynamodb.model.ReturnConsumedCapacity
import org.http4k.connect.amazon.dynamodb.model.ReturnItemCollectionMetrics
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class BatchWriteItem(
    val RequestItems: Map<TableName, List<ReqWriteItem>>,
    val ReturnItemCollectionMetrics: ReturnItemCollectionMetrics? = null,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null
) : DynamoDbAction<BatchWriteItems>(BatchWriteItems::class, DynamoDbMoshi)

@JsonSerializable
data class BatchWriteItems(
    val ConsumedCapacity: List<ConsumedCapacity>? = null,
    val Responses: Map<TableName, TokensToNames>? = null,
    val UnprocessedKeys: Map<String, ReqWriteItem>? = null)
