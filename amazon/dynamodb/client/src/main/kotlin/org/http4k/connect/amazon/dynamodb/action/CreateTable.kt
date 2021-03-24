package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.amazon.model.Tag
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class CreateTable(
    val TableName: TableName,
    val KeySchema: List<KeySchema>,
    val AttributeDefinitions: List<AttributeDefinition>,
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndex>? = null,
    val LocalSecondaryIndexes: List<LocalSecondaryIndexes>? = null,
    val Tags: List<Tag>? = null,
    val BillingMode: BillingMode? = null,
    val ProvisionedThroughput: ProvisionedThroughput? = null,
    val SSESpecification: SSESpecification? = null,
    val StreamSpecification: StreamSpecification? = null
) : DynamoDbAction<TableDescriptionResponse>(TableDescriptionResponse::class, DynamoDbMoshi)

