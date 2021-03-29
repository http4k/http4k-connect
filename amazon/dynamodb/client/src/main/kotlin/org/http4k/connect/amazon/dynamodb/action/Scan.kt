package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
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
): PagedAction<ScanResponse>(ScanResponse::class, DynamoDbMoshi) {
    override fun next(lastKey: Key) = copy(ExclusiveStartKey = lastKey)
}

@JsonSerializable
data class ScanResponse(
    val ConsumedCapacity: ConsumedCapacity?,
    val Count: Int?,
    override val LastEvaluatedKey: Key?,
    val ScannedCount: Int?,
    internal val Items: List<ItemResult>?
) :Paged {
    override val items = Items?.map(ItemResult::toItem) ?: emptyList()
}
