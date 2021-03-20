package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class BatchGetItem(
    val RequestItems: Map<TableName, ReqGetItem>,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null
) : DynamoDbAction<BatchGetItems>(BatchGetItems::class, DynamoDbMoshi)

@JsonSerializable
data class ReqGetItem(
    val Keys: List<NamesToValues>,
    val ProjectionExpression: String? = null,
    val ExpressionAttributeNames: TokensToNames? = null,
    val ConsistentRead: Boolean? = null
)

@JsonSerializable
data class BatchGetItems(
    val ConsumedCapacity: List<ConsumedCapacity>?,
    val Responses: Map<String, NamesToValues>?,
    val UnprocessedKeys: Map<String, ReqGetItem>?
)
