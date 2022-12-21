package org.http4k.connect.amazon.dynamodb.endpoints

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.dynamodb.DynamoDbSource
import org.http4k.connect.amazon.dynamodb.FakeDynamoDbSource
import org.http4k.connect.amazon.dynamodb.describeImport
import org.http4k.connect.amazon.dynamodb.importTable
import org.http4k.connect.amazon.dynamodb.model.AttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.ClientToken
import org.http4k.connect.amazon.dynamodb.model.CsvOptions
import org.http4k.connect.amazon.dynamodb.model.DynamoDataType
import org.http4k.connect.amazon.dynamodb.model.ImportStatus
import org.http4k.connect.amazon.dynamodb.model.InputCompressionType
import org.http4k.connect.amazon.dynamodb.model.InputFormat
import org.http4k.connect.amazon.dynamodb.model.InputFormatOptions
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.KeyType
import org.http4k.connect.amazon.dynamodb.model.ProvisionedThroughput
import org.http4k.connect.amazon.dynamodb.model.S3BucketSource
import org.http4k.connect.amazon.dynamodb.model.TableCreationParameters
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.sample
import org.http4k.connect.successValue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

abstract class ImportTableContract : DynamoDbSource {
    @Test
    fun `create a new import table`() {
        val importArn = dynamo.importTable(
            ClientToken = ClientToken.random(),
            InputCompressionType = InputCompressionType.NONE,
            S3BucketSource = S3BucketSource(S3Bucket = "a-bucket"),
            InputFormat = InputFormat.ION,
            InputFormatOptions = InputFormatOptions(CsvOptions(Delimiter = ',', HeaderList = listOf("header 1"))),
            TableCreationParameters = TableCreationParameters(
                KeySchema = listOf(KeySchema(AttributeName.of("ID"), KeyType.HASH)),
                TableName = TableName.sample(),
                AttributeDefinitions = listOf(
                    AttributeDefinition(
                        AttributeName.of("ID"),
                        AttributeType = DynamoDataType.S
                    )
                ),
                BillingMode = BillingMode.PROVISIONED,
                ProvisionedThroughput = ProvisionedThroughput(ReadCapacityUnits = 5, WriteCapacityUnits = 5)
            )
        ).successValue().ImportTableDescription.ImportArn!!
        
        val import = dynamo.describeImport(importArn).successValue().ImportTableDescription
        assertThat(import.ImportStatus, equalTo(ImportStatus.IN_PROGRESS))
        assertThat(import.S3BucketSource?.S3Bucket, equalTo("a-bucket"))
    }
}

class FakeImportTableTest : ImportTableContract(), DynamoDbSource by FakeDynamoDbSource()
