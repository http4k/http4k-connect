package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.IndexName
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class Query(
    val TableName: TableName,
    val KeyConditionExpression: String? = null,
    val FilterExpression: String? = null,
    val ProjectionExpression: String? = null,
    val IndexName: IndexName? = null,
    val ExpressionAttributeNames: TokensToNames? = null,
    val ExpressionAttributeValues: TokensToValues? = null,
    val Select: Select? = null,
    val ConsistentRead: Boolean? = null,
    val ExclusiveStartKey: Key? = null,
    val Limit: Int? = null,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null,
    val ScanIndexForward: Boolean? = null,
) : PagedAction<QueryResponse>(QueryResponse::class, DynamoDbMoshi) {
    override fun next(lastKey: Key) = copy(ExclusiveStartKey = lastKey)
}

@JsonSerializable
data class QueryResponse(
    internal val Items: List<ItemResult>?,
    val ConsumedCapacity: ConsumedCapacity?,
    val Count: Int?,
    override val LastEvaluatedKey: Key?,
    val ScannedCount: Int?
) : Paged {
    override val items = Items?.map(ItemResult::toItem) ?: emptyList()
}

enum class Select {
    ALL_ATTRIBUTES, ALL_PROJECTED_ATTRIBUTES, SPECIFIC_ATTRIBUTES, COUNT
}
