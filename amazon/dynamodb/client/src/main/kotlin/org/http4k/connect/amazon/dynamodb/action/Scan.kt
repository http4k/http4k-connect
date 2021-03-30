package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.Paged
import org.http4k.connect.amazon.PagedAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class Scan(
    val TableName: TableName,
    val FilterExpression: String? = null,
    val ExpressionAttributeNames: TokensToNames? = null,
    val ExpressionAttributeValues: TokensToValues? = null,
    val ExclusiveStartKey: Key? = null,
    val IndexName: String? = null,
    val Limit: Int? = null,
    val ConsistentRead: Boolean? = null,
    val ProjectionExpression: String? = null,
    val Segment: Int? = null,
    val Select: String? = null,
    val TotalSegments: Int? = null,
    val ReturnConsumedCapacity: String? = null,
): DynamoDbAction<ScanResponse>(ScanResponse::class, DynamoDbMoshi),
    PagedAction<Key, Item, ScanResponse, Scan> {
    override fun next(token: Key) = copy(ExclusiveStartKey = token)
}

@JsonSerializable
data class ScanResponse(
    val ConsumedCapacity: ConsumedCapacity?,
    val Count: Int?,
    val LastEvaluatedKey: Key?,
    val ScannedCount: Int?,
    internal val Items: List<ItemResult>?
) : Paged<Key, Item> {
    override val items = Items?.map(ItemResult::toItem) ?: emptyList()
    override fun token() = LastEvaluatedKey
}
