package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.model.ImportSummary
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class ListImports(
    val NextToken: String? = null,
) : DynamoDbAction<ListImportsResponse>(ListImportsResponse::class, DynamoDbMoshi)

@JsonSerializable
data class ListImportsResponse(
    val ImportSummaryList: List<ImportSummary>,
    val NextToken: String? = null
)
