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
data class ReqGetItem internal constructor(
    val Keys: List<Item>,
    val ProjectionExpression: String? = null,
    val ExpressionAttributeNames: TokensToNames? = null,
    val ConsistentRead: Boolean? = null
) {
    companion object {
        fun Get(
            Keys: List<Item>,
            ProjectionExpression: String? = null,
            ExpressionAttributeNames: TokensToNames? = null,
            ConsistentRead: Boolean? = null
        ) = ReqGetItem(Keys, ProjectionExpression, ExpressionAttributeNames, ConsistentRead)
    }
}

@JsonSerializable
data class BatchGetItems(
    val ConsumedCapacity: List<ConsumedCapacity>?,
    val Responses: Map<String, List<Item>>?,
    val UnprocessedItems: Map<String, ReqGetItem>?
)
