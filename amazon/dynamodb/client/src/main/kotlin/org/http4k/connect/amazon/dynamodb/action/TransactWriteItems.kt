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
data class TransactWriteItem internal constructor(
    val ConditionCheck: Map<String, Any?>? = null,
    val Delete: Map<String, Any?>? = null,
    val Put: Map<String, Any?>? = null,
    val Update: Map<String, Any?>? = null
) {
    companion object {
        fun ConditionCheck(
            TableName: TableName,
            Key: Key,
            ConditionExpression: String,
            ExpressionAttributeNames: TokensToNames? = null,
            ExpressionAttributeValues: TokensToValues? = null,
            ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
        ) = TransactWriteItem(
            ConditionCheck = mapOf(
                "TableName" to TableName,
                "ConditionExpression" to ConditionExpression,
                "Key" to Key,
                "ExpressionAttributeNames" to ExpressionAttributeNames,
                "ExpressionAttributeValues" to ExpressionAttributeValues,
                "ReturnValuesOnConditionCheckFailure" to ReturnValuesOnConditionCheckFailure
            )
        )

        fun Delete(
            TableName: TableName,
            Key: Key,
            ConditionExpression: String? = null,
            ExpressionAttributeNames: TokensToNames? = null,
            ExpressionAttributeValues: TokensToValues? = null,
            ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
        ) = TransactWriteItem(
            Delete = mapOf(
                "TableName" to TableName,
                "ConditionExpression" to ConditionExpression,
                "Key" to Key,
                "ExpressionAttributeNames" to ExpressionAttributeNames,
                "ExpressionAttributeValues" to ExpressionAttributeValues,
                "ReturnValuesOnConditionCheckFailure" to ReturnValuesOnConditionCheckFailure
            )
        )

        fun Put(
            TableName: TableName,
            Item: Item,
            ConditionExpression: String? = null,
            ExpressionAttributeNames: TokensToNames? = null,
            ExpressionAttributeValues: TokensToValues? = null,
            ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
        ) = TransactWriteItem(
            Put = mapOf(
                "TableName" to TableName,
                "ConditionExpression" to ConditionExpression,
                "ExpressionAttributeNames" to ExpressionAttributeNames,
                "ExpressionAttributeValues" to ExpressionAttributeValues,
                "Item" to Item,
                "ReturnValuesOnConditionCheckFailure" to ReturnValuesOnConditionCheckFailure
            )
        )

        fun Update(
            TableName: TableName,
            Key: Key,
            UpdateExpression: String,
            ConditionExpression: String? = null,
            ExpressionAttributeNames: TokensToNames? = null,
            ExpressionAttributeValues: TokensToValues? = null,
            ReturnValuesOnConditionCheckFailure: ReturnValuesOnConditionCheckFailure? = null
        ) = TransactWriteItem(
            Update = mapOf(
                "TableName" to TableName,
                "ConditionExpression" to ConditionExpression,
                "Key" to Key,
                "UpdateExpression" to UpdateExpression,
                "ExpressionAttributeNames" to ExpressionAttributeNames,
                "ExpressionAttributeValues" to ExpressionAttributeValues,
                "ReturnValuesOnConditionCheckFailure" to ReturnValuesOnConditionCheckFailure
            )
        )
    }
}

@JsonSerializable
enum class ReturnValuesOnConditionCheckFailure {
    ALL_OLD, NONE
}
