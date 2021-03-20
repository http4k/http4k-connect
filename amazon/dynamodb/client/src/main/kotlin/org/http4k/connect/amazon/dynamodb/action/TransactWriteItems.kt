package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class TransactWriteItems(
    val TransactItems: List<TransactWriteItem>,
    val ClientRequestToken: String? = null,
    val ReturnConsumedCapacity: ReturnConsumedCapacity? = null,
    val ReturnItemCollectionMetrics: ReturnItemCollectionMetrics? = null,
) : DynamoDbAction<ModifiedItems>(ModifiedItems::class, DynamoDbMoshi)

@JsonSerializable
data class ModifiedItems(
    val ConsumedCapacity: ConsumedCapacity?,
    val ItemCollectionMetrics: ItemCollectionMetrics?
)

@JsonSerializable
data class TransactWriteItem(
    val ConditionCheck: ConditionCheck?,
    val Delete: Delete?,
    val Put: Put?,
    val Update: Update?
)

@JsonSerializable
data class ConditionCheck(
    val TableName: TableName,
    val ConditionExpression: String,
    val Key: AttributeValues,
    val ExpressionAttributeNames: AttributeNames? = null,
    val ExpressionAttributeValues: AttributeValues? = null,
    val ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
)

@JsonSerializable
enum class ReturnValuesOnConditionCheckFailure {
    ALL_OLD, NONE
}

@JsonSerializable
data class Delete(
    val TableName: TableName,
    val ConditionExpression: String,
    val Key: AttributeValues,
    val ExpressionAttributeNames: AttributeNames? = null,
    val ExpressionAttributeValues: AttributeValues? = null,
    val ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
)

@JsonSerializable
data class Put(
    val TableName: TableName,
    val ConditionExpression: String,
    val ExpressionAttributeNames: AttributeNames? = null,
    val ExpressionAttributeValues: AttributeValues? = null,
    val Item: AttributeValues?,
    val ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
)

@JsonSerializable
data class Update(
    val TableName: TableName,
    val ConditionExpression: String?,
    val Key: AttributeValues,
    val UpdateExpression: String? = null,
    val ExpressionAttributeNames: AttributeNames? = null,
    val ExpressionAttributeValues: AttributeValues? = null,
    val ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
)
