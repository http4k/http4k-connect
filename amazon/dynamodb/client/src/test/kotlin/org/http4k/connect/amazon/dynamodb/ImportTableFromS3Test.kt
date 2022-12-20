package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.connect.amazon.core.model.ARN
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
import org.http4k.filter.debug
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.*

class ImportTableFromS3Test: RealAwsEnvironment {
    private val http = JavaHttpClient().debug()
    
    override val aws = configAwsEnvironment()

    private val dynamo = DynamoDb.Http(aws.region, { aws.credentials }, http)
    private val s3 = S3.Http({ aws.credentials }, http)

    private val bucket = BucketName.sample()
    private val tableName = TableName.sample()

    @BeforeEach
    fun createBucket() {
        val s3Bucket = S3Bucket.Http(bucket, aws.region, { aws.credentials }, http)
        s3.createBucket(bucket, aws.region).successValue()
        s3.waitForBucketCreated(bucket)
        s3Bucket.putObject(
            key = BucketKey.of("data.csv"),
            content = """
                ID,AGE
                1,42""".trimIndent().byteInputStream(),
            headers = emptyList() 
        ).successValue()
    }
    
    @Test
    fun `import is successful`() {
        val import = dynamo.importTable(
            sourceBucket = bucket.value, 
            tableName = tableName
        ).successValue().ImportTableDescription
        dynamo.waitForImportFinished(import.ImportArn!!, timeout = Duration.ofMinutes(3))

        val currentImportDescription =
            dynamo.describeImport(import.ImportArn!!).successValue().ImportTableDescription

        assertThat(currentImportDescription.ImportStatus, equalTo(ImportStatus.COMPLETED))
    }

    @Test
    fun `import table fails where the S3 bucket does not exist`() {
        val import = dynamo.importTable(sourceBucket = "i-do-no-exist").successValue().ImportTableDescription
        dynamo.waitForImportFinished(import.ImportArn!!)

        val currentImportDescription = dynamo.describeImport(import.ImportArn!!).successValue().ImportTableDescription

        assertThat(currentImportDescription.ImportStatus, equalTo(ImportStatus.FAILED))
    }
    
    @Test
    fun `query table imports`() {
        val import = dynamo.importTable(sourceBucket = "i-do-no-exist").successValue().ImportTableDescription

        val importSummaries = dynamo.listImports(TableArn = import.TableArn!!).successValue().ImportSummaryList

        assertThat(importSummaries.map { it.ImportArn }.contains(import.ImportArn!!), equalTo(true))
    }

    @AfterEach
    fun deleteBucket() {
        val s3Bucket = S3Bucket.Http(bucket, aws.region, { aws.credentials }, http)
        s3Bucket.listObjectsV2().successValue().forEach { s3Bucket.deleteObject(it.Key) }
        s3Bucket.deleteBucket()
    }

    @AfterEach
    fun deleteTable() {
        val (tables, _) = dynamo.listTables().successValue()
        if (tables.contains(tableName)) {
            dynamo.deleteTable(tableName).successValue()
        }
    }
}

private fun DynamoDb.importTable(sourceBucket: String, tableName: TableName = TableName.sample()) = importTable(
    ClientToken = ClientToken.random(),
    InputCompressionType = InputCompressionType.NONE,
    S3BucketSource = S3BucketSource(S3Bucket = sourceBucket),
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
)

private fun DynamoDb.waitForImportFinished(importArn: ARN, timeout: Duration = Duration.ofSeconds(20)) {
    val waitStart = Instant.now()
    while (Duration.between(waitStart, Instant.now()) < timeout) {
        if (describeImport(importArn).successValue().ImportTableDescription.ImportStatus != ImportStatus.IN_PROGRESS) {
            return
        }
        Thread.sleep(1000)
    }
    throw IllegalStateException("Import $importArn was not finished after $timeout")
}

private fun S3.waitForBucketCreated(bucketName: BucketName, timeout: Duration = Duration.ofSeconds(10)) {
    val waitStart = Instant.now()
    while (Duration.between(waitStart, Instant.now()) < timeout) {
        if (listBuckets().successValue().items.contains(bucketName)) {
            return
        }
        Thread.sleep(1000)
    }
    throw IllegalStateException("Bucket $bucketName was not created after $timeout")
}

private fun BucketName.Companion.sample() = BucketName.of("http4k-connect-${UUID.randomUUID()}")
