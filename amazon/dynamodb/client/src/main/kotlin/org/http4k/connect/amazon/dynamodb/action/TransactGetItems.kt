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
) : DynamoDbAction<ModifiedItems>(ModifiedItems::class, DynamoDbMoshi)

@JsonSerializable
data class Get(
    val TableName: TableName,
    val Key: AttributeValues,
    val ProjectionExpression: String? = null,
    val ExpressionAttributeNames: AttributeNames? = null
)

@JsonSerializable
data class TransactGetItem(val Get: Get)
