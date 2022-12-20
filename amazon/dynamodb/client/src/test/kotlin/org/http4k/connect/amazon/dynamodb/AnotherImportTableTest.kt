package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.Success
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.dynamodb.action.DescribedTable
import org.http4k.connect.amazon.dynamodb.model.AttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
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
import org.http4k.connect.amazon.s3.Http
import org.http4k.connect.amazon.s3.S3
import org.http4k.connect.amazon.s3.S3Bucket
import org.http4k.connect.amazon.s3.createBucket
import org.http4k.connect.amazon.s3.deleteBucket
import org.http4k.connect.amazon.s3.deleteObject
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.amazon.s3.putObject
import org.http4k.connect.successValue
import org.http4k.filter.debug
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.*

class AnotherImportTableTest {
    private val aws = configAwsEnvironment()
    private val dynamo = DynamoDb.Http(aws.region, { aws.credentials }, JavaHttpClient().debug())

    @Test
    fun `import table fails where the S3 bucket does not exist`() {
        val requestedImport = dynamo.importTable(
            ClientToken = ClientToken.random(),
            InputCompressionType = InputCompressionType.NONE,
            S3BucketSource = S3BucketSource(S3Bucket = "i-do-not-exist"),
            InputFormat = InputFormat.CSV,
            InputFormatOptions = InputFormatOptions(CsvOptions(Delimiter = ',')),
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
        ).successValue()

        val importArn = requestedImport.ImportTableDescription.ImportArn!!
        val importStatus = dynamo.describeImport(importArn).successValue()
        val imports = dynamo.listImports(TableArn = importStatus.ImportTableDescription.TableArn).successValue()
        assertThat(imports.ImportSummaryList.map { it.ImportArn }.contains(importStatus.ImportTableDescription.ImportArn), equalTo(true))
        dynamo.waitForImportFinished(importArn)
        assertThat(dynamo.describeImport(importArn).successValue().ImportTableDescription.ImportStatus, equalTo(ImportStatus.FAILED))
    }

    @Test
    fun `import is successful`() {
        val http = JavaHttpClient().debug()
        val s3 = S3.Http({ aws.credentials }, http)
        val bucket = BucketName.sample()
        val s3Bucket = S3Bucket.Http(bucket, aws.region, { aws.credentials }, http)

        s3.createBucket(bucket, aws.region).successValue()
        waitForUpdate()
        val data = """
            ID,AGE
            1,42""".trimIndent()
        val key = BucketKey.of("data.csv")
        s3Bucket.putObject(key, data.byteInputStream(), emptyList()).successValue()
        val tableName = TableName.sample()
        try {
            val requestedImport = dynamo.importTable(
                ClientToken = ClientToken.random(),
                InputCompressionType = InputCompressionType.NONE,
                S3BucketSource = S3BucketSource(S3Bucket = bucket.toString()),
                InputFormat = InputFormat.CSV,
                InputFormatOptions = InputFormatOptions(CsvOptions(Delimiter = ',')),
                TableCreationParameters = TableCreationParameters(
                    KeySchema = listOf(KeySchema(AttributeName.of("ID"), KeyType.HASH)),
                    TableName = tableName,
                    AttributeDefinitions = listOf(
                        AttributeDefinition(
                            AttributeName.of("ID"),
                            AttributeType = DynamoDataType.S
                        )
                    ),
                    BillingMode = BillingMode.PROVISIONED,
                    ProvisionedThroughput = ProvisionedThroughput(ReadCapacityUnits = 5, WriteCapacityUnits = 5)
                )
            ).successValue()

            val importArn = requestedImport.ImportTableDescription.ImportArn!!
            dynamo.waitForImportFinished(importArn, timeout = Duration.ofMinutes(3))

            assertThat(dynamo.describeImport(importArn).successValue().ImportTableDescription.ImportStatus, equalTo(ImportStatus.COMPLETED))
            val item = dynamo.getItem(tableName, Key = mapOf(AttributeName.of("ID") to AttributeValue.Str("1"))).successValue().item
            assertThat(item, present())

        } finally {
            s3Bucket.deleteObject(key)
            s3Bucket.deleteBucket()
            val (tables, _) = dynamo.listTables().successValue()
            if (tables.contains(tableName)) {
                dynamo.deleteTable(tableName).successValue()
            }
        }
    }

}

private fun DynamoDb.waitForImportFinished(importArn: ARN, timeout: Duration = Duration.ofSeconds(10)) {
    val waitStart = Instant.now()
    while (Duration.between(waitStart, Instant.now()) < timeout) {
        if (describeImport(importArn).successValue().ImportTableDescription.ImportStatus != ImportStatus.IN_PROGRESS) {
            return
        }
        Thread.sleep(1000)
    }
    throw IllegalStateException("Import $importArn was not finished after $timeout")
}

private fun waitForUpdate() = Thread.sleep(Duration.ofSeconds(10).toMillis())

private fun BucketName.Companion.sample() = BucketName.of("http4k-connect-${UUID.randomUUID()}")
