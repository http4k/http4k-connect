package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class TransactGetItems(
    val TransactItems: List<TransactGetItem>,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null
) : DynamoDbAction<GetItemsResponse>(GetItemsResponse::class, DynamoDbMoshi)

@JsonSerializable
data class Get(
    val TableName: TableName,
    val Key: ItemAttributes,
    val ProjectionExpression: String? = null,
    val ExpressionAttributeNames: TokensToNames? = null
)

@JsonSerializable
data class TransactGetItem internal constructor(val Get: Map<String, Any?>) {
    companion object {
        fun Get(
            TableName: TableName,
            Key: ItemAttributes,
            ProjectionExpression: String? = null,
            ExpressionAttributeNames: TokensToNames? = null
        ) = TransactGetItem(
            Get = mapOf(
                "TableName" to TableName,
                "Key" to Key,
                "ProjectionExpression" to ProjectionExpression,
                "ExpressionAttributeNames" to ExpressionAttributeNames
            )
        )
    }
}

@JsonSerializable
data class GetItemsResponse(
    val ConsumedCapacity: ConsumedCapacity?,
    val ItemCollectionMetrics: ItemCollectionMetrics?,
    internal val Responses: List<GetItemsResponseItem>) {
    val responses = Responses.map { it.Item }.map(ItemResult::toItem)
}

@JsonSerializable
data class GetItemsResponseItem(val Item: ItemResult)
