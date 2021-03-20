package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class BatchExecuteStatement(
    val Statements: List<ReqStatement>
) : DynamoDbAction<BatchStatements>(BatchStatements::class, DynamoDbMoshi)

@JsonSerializable
data class ReqStatement(
    val Statement: String,
    val ConsistentRead: Boolean? = null,
    val Parameters: List<AttributeValues>? = null
)

@JsonSerializable
data class BatchStatements(val Responses: List<StatementResponse>)

enum class ErrorCode {
    ConditionalCheckFailed, ItemCollectionSizeLimitExceeded, RequestLimitExceeded, ValidationError, ProvisionedThroughputExceeded, TransactionConflict, ThrottlingError, InternalServerError, ResourceNotFound, AccessDenied, DuplicateItem
}

@JsonSerializable
data class BatchStatementError(
    val Code: ErrorCode?,
    val Message: String?
)

@JsonSerializable
data class StatementResponse(
    val TableName: TableName,
    val Error: BatchStatementError?,
    internal val Item: ItemResult?
) {
    val item = Item?.toItem() ?: emptyMap()
}
