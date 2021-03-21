package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.AttributeDefinition
import org.http4k.connect.amazon.model.BillingMode
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class UpdateTable(
    val TableName: TableName,
    val AttributeDefinitions: List<AttributeDefinition>? = null,
    val BillingMode: BillingMode? = null,
    val GlobalSecondaryIndexUpdates: List<GlobalSecondaryIndexUpdates>? = null,
    val ProvisionedThroughput: ProvisionedThroughput? = null,
    val ReplicaUpdates: List<ReplicaUpdates>? = null,
    val SSESpecification: SSESpecification? = null,
    val StreamSpecification: StreamSpecification? = null
) :
    DynamoDbAction<TableDescriptionResponse>(TableDescriptionResponse::class, DynamoDbMoshi)
