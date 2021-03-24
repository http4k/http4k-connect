package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class GetItem(
    val TableName: TableName,
    val Key: Key,
    val ProjectionExpression: String? = null,
    val ExpressionAttributeNames: TokensToNames? = null,
    val ConsistentRead: Boolean? = null,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null
) : DynamoDbAction<GetResponse>(GetResponse::class, DynamoDbMoshi)

@JsonSerializable
data class GetResponse(
    val ConsumedCapacity: ConsumedCapacity?,
    internal val Item: ItemResult?
) {
    val item = Item?.toItem()
}
