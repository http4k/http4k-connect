package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.model.ClientToken
import org.http4k.connect.amazon.dynamodb.model.ImportTableDescription
import org.http4k.connect.amazon.dynamodb.model.InputCompressionType
import org.http4k.connect.amazon.dynamodb.model.InputFormat
import org.http4k.connect.amazon.dynamodb.model.InputFormatOptions
import org.http4k.connect.amazon.dynamodb.model.S3BucketSource
import org.http4k.connect.amazon.dynamodb.model.TableCreationParameters
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class DescribeImport(
    val ImportArn: ARN
) : DynamoDbAction<ImportTableResponse>(ImportTableResponse::class, DynamoDbMoshi)

