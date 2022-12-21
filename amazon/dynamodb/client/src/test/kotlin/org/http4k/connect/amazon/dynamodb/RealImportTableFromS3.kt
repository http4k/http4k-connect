package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.natpryce.hamkrest.present
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.AttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.ClientToken
import org.http4k.connect.amazon.dynamodb.model.CsvOptions
import org.http4k.connect.amazon.dynamodb.model.DynamoDataType
import org.http4k.connect.amazon.dynamodb.model.ImportStatus
import org.http4k.connect.amazon.dynamodb.model.ImportStatus.COMPLETED
import org.http4k.connect.amazon.dynamodb.model.ImportStatus.FAILED
import org.http4k.connect.amazon.dynamodb.model.InputCompressionType.NONE
import org.http4k.connect.amazon.dynamodb.model.InputFormat
import org.http4k.connect.amazon.dynamodb.model.InputFormat.CSV
import org.http4k.connect.amazon.dynamodb.model.InputFormatOptions
import org.http4k.connect.amazon.dynamodb.model.Key
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
import org.http4k.connect.amazon.s3.listBuckets
import org.http4k.connect.amazon.s3.listObjectsV2
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.amazon.s3.putObject
import org.http4k.connect.successValue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.*

class RealImportTableFromS3Test : AwsContract() {
    override val aws = configAwsEnvironment()
    private val http = JavaHttpClient()

    private val dynamo by lazy {
        DynamoDb.Http(aws.region, { aws.credentials }, http)
    }

    @Test
    fun `import table is successful`() {
        val table = TableName.sample()
        val bucket = BucketName.sample().also {
            it.create()
            it.uploadCsv("ID,AGE\n1,42")
        }
        try {
            val clientToken = ClientToken.random()
            val tableCreationParameters = TableCreationParameters(
                KeySchema = listOf(KeySchema(AttributeName.of("ID"), KeyType.HASH)),
                TableName = table,
                AttributeDefinitions = listOf(
                    AttributeDefinition(
                        AttributeName.of("ID"),
                        AttributeType = DynamoDataType.S
                    )
                ),
                BillingMode = BillingMode.PROVISIONED,
                ProvisionedThroughput = ProvisionedThroughput(ReadCapacityUnits = 5, WriteCapacityUnits = 5)
            )
            val importArn = dynamo.importTable(
                ClientToken = clientToken,
                InputCompressionType = NONE,
                S3BucketSource = S3BucketSource(S3Bucket = bucket.value),
                InputFormat = CSV,
                InputFormatOptions = InputFormatOptions(CsvOptions(Delimiter = ',')),
                TableCreationParameters = tableCreationParameters
            ).successValue().ImportTableDescription.ImportArn!!
            dynamo.waitForImportFinished(importArn, timeout = Duration.ofMinutes(3))

            with(dynamo.describeImport(importArn).successValue().ImportTableDescription) {
                assertThat(ClientToken, equalTo(clientToken))
                assertThat(CloudWatchLogGroupArn, present())
                assertThat(StartTime, present())
                assertThat(EndTime, present())
                assertThat(ErrorCount, equalTo(0))
                assertThat(FailureCode, absent())
                assertThat(FailureMessage, absent())
                assertThat(ImportArn?.awsService, equalTo(AwsService.of("dynamodb")))
                assertThat(ImportStatus, equalTo(COMPLETED))
                assertThat(InputCompressionType, equalTo(NONE))
                assertThat(InputFormat, equalTo(CSV))
                assertThat(InputFormatOptions, equalTo(InputFormatOptions(CsvOptions(Delimiter = ','))))
                assertThat(ProcessedItemCount, present(greaterThanOrEqualTo(0)))
                assertThat(ProcessedSizeBytes, present(greaterThanOrEqualTo(0)))
                assertThat(S3BucketSource?.S3Bucket, equalTo(bucket.value))
                assertThat(TableArn?.awsService, equalTo(AwsService.of("dynamodb")))
                assertThat(TableCreationParameters, equalTo(tableCreationParameters))
                assertThat(TableId, present())
            }
            assertThat(dynamo.getItem(table, key = "ID", value = "1"), present())
        } finally {
            bucket.delete()
            table.delete()
        }
    }

    @Test
    fun `import table fails where the S3 bucket does not exist`() {
        val importArn = dynamo.importTable(sourceBucket = BucketName.sample()).successValue().ImportTableDescription.ImportArn!!
        dynamo.waitForImportFinished(importArn)

        with(dynamo.describeImport(importArn).successValue().ImportTableDescription) {
            assertThat(ImportStatus, equalTo(FAILED))
            assertThat(FailureCode, present())
            assertThat(FailureMessage, present())
        }
    }

    @Test
    fun `query table imports by table ARN`() {
        val import = dynamo.importTable(sourceBucket = BucketName.sample()).successValue().ImportTableDescription

        val importSummaries = dynamo.listImports(TableArn = import.TableArn!!).successValue().ImportSummaryList

        assertThat(importSummaries.map { it.ImportArn }.contains(import.ImportArn!!), equalTo(true))
    }

    private fun BucketName.uploadCsv(csv: String) {
        val s3Bucket = S3Bucket.Http(this, aws.region, { aws.credentials }, http)
        s3Bucket.putObject(
            key = BucketKey.of("data.csv"),
            content = csv.byteInputStream(),
            headers = emptyList()
        ).successValue()
    }

    private fun BucketName.create() {
        val s3 = S3.Http({ aws.credentials }, http)
        s3.createBucket(this, aws.region).successValue()
        s3.waitForBucketCreated(this)
    }

    private fun BucketName.delete() {
        with(S3Bucket.Http(this, aws.region, { aws.credentials }, http)) {
            listObjectsV2().successValue().forEach { deleteObject(it.Key) }
            deleteBucket()
        }
    }

    private fun TableName.delete() {
        val (tables, _) = dynamo.listTables().successValue()
        if (tables.contains(this)) {
            dynamo.deleteTable(this).successValue()
        }
    }
}

private fun DynamoDb.importTable(
    sourceBucket: BucketName,
    inputFormat: InputFormat = CSV,
    tableName: TableName = TableName.sample(),
    key: String = "ID"
) = importTable(
    ClientToken = ClientToken.random(),
    InputCompressionType = NONE,
    S3BucketSource = S3BucketSource(S3Bucket = sourceBucket.value),
    InputFormat = inputFormat,
    InputFormatOptions = InputFormatOptions(CsvOptions(Delimiter = ',')),
    TableCreationParameters = TableCreationParameters(
        KeySchema = listOf(KeySchema(AttributeName.of(key), KeyType.HASH)),
        TableName = tableName,
        AttributeDefinitions = listOf(
            AttributeDefinition(
                AttributeName.of(key),
                AttributeType = DynamoDataType.S
            )
        ),
        BillingMode = BillingMode.PROVISIONED,
        ProvisionedThroughput = ProvisionedThroughput(ReadCapacityUnits = 5, WriteCapacityUnits = 5)
    )
)

private fun DynamoDb.waitForImportFinished(importArn: ARN, timeout: Duration = Duration.ofSeconds(20)) {
    waitUntil(
        { describeImport(importArn).successValue().ImportTableDescription.ImportStatus != ImportStatus.IN_PROGRESS },
        failureMessage = "Import $importArn was not finished after $timeout",
        timeout = timeout
    )
}

private fun S3.waitForBucketCreated(bucketName: BucketName, timeout: Duration = Duration.ofSeconds(10)) {
    waitUntil(
        { listBuckets().successValue().items.contains(bucketName) },
        failureMessage = "Bucket $bucketName was not created after $timeout",
        timeout = timeout
    )
}

private fun waitUntil(test: () -> Boolean, failureMessage: String, timeout: Duration) {
    val waitStart = Instant.now()
    while (Duration.between(waitStart, Instant.now()) < timeout) {
        if (test()) {
            return
        }
        Thread.sleep(1000)
    }
    throw IllegalStateException(failureMessage)
}

private fun DynamoDb.getItem(tableName: TableName, key: String, value: String) =
    getItem(tableName, Key(Attribute.string().required(key) of value)).successValue().item

private fun BucketName.Companion.sample() = BucketName.of("http4k-connect-${UUID.randomUUID()}")
