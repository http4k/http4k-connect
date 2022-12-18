package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Paged
import org.http4k.connect.PagedAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.model.ConsumedCapacity
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.ItemResult
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.ReturnConsumedCapacity
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues
import org.http4k.connect.amazon.dynamodb.model.toItem
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class ImportTable(
    val TableName: TableName,
) : DynamoDbAction<ImportTableResponse>(ImportTableResponse::class, DynamoDbMoshi)

@JsonSerializable
data class ImportTableResponse(
    val ConsumedCapacity: Int? = null,
)
