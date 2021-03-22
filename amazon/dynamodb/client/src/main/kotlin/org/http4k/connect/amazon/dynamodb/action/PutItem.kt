package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class PutItem(
    val TableName: TableName,
    val Item: ItemAttributes,
    val ConditionExpression: String? = null,
    val ExpressionAttributeNames: TokensToNames? = null,
    val ExpressionAttributeValues: TokensToValues? = null,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null,
    val ReturnItemCollectionMetrics: ReturnItemCollectionMetrics? = null,
    val ReturnValues: ReturnValues? = null
) : DynamoDbAction<ModifiedItem>(ModifiedItem::class, DynamoDbMoshi)
