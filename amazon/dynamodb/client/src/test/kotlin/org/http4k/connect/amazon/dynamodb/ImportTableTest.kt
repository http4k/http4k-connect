package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.natpryce.hamkrest.present
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.http4k.aws.AwsCredentials
import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.dynamodb.model.AttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.BillingMode.PAY_PER_REQUEST
import org.http4k.connect.amazon.dynamodb.model.ClientToken
import org.http4k.connect.amazon.dynamodb.model.CsvOptions
import org.http4k.connect.amazon.dynamodb.model.DynamoDataType.S
import org.http4k.connect.amazon.dynamodb.model.ImportStatus
import org.http4k.connect.amazon.dynamodb.model.InputCompressionType
import org.http4k.connect.amazon.dynamodb.model.InputFormat
import org.http4k.connect.amazon.dynamodb.model.InputFormatOptions
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.KeyType
import org.http4k.connect.amazon.dynamodb.model.S3BucketSource
import org.http4k.connect.amazon.dynamodb.model.TableCreationParameters
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.filter.debug
import org.http4k.format.MapAdapter
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.ImportTableRequest
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.dynamodb.model.BillingMode as AwsBillingMode
import software.amazon.awssdk.services.dynamodb.model.InputCompressionType as AwsInputCompressionType
import software.amazon.awssdk.services.dynamodb.model.InputFormat as AwsInputFormat
import software.amazon.awssdk.services.dynamodb.model.KeyType as AwsKeyType

class ImportTableTest {
    @Test
    fun `sends same request as AWS SDK client`() {
        var awsClientRequest: Request? = null
        val awsDdb = awsClient(recordingRequest { awsClientRequest = it })
        var http4kClientRequest: Request? = null
        val http4kDdb = http4kClient(recordingRequest { http4kClientRequest = it })

        awsDdb.importTable(
            ImportTableRequest.builder()
                .clientToken("client token")
                .inputCompressionType(AwsInputCompressionType.NONE)
                .s3BucketSource { it.s3Bucket("bucket").s3BucketOwner("owner").s3KeyPrefix("prefix") }
                .inputFormat(AwsInputFormat.CSV)
                .inputFormatOptions { 
                    it.csv { csv -> csv.delimiter(",").headerList("header 1", "header 2") }
                }
                .tableCreationParameters { table ->
                    table
                        .keySchema({
                            it.attributeName("key").keyType(AwsKeyType.HASH)
                        })
                        .tableName("test")
                        .attributeDefinitions({ it.attributeName("key").attributeType(ScalarAttributeType.S) })
                        .billingMode(AwsBillingMode.PAY_PER_REQUEST)
                }
                .build()
        )
        http4kDdb.importTable(
            ClientToken = ClientToken.of("client token"),
            InputCompressionType = InputCompressionType.NONE,
            S3BucketSource = S3BucketSource(S3Bucket = "bucket", S3BucketOwner = "owner", S3KeyPrefix = "prefix"),
            InputFormat = InputFormat.CSV,
            InputFormatOptions = InputFormatOptions(CsvOptions(Delimiter = ',', HeaderList = listOf("header 1", "header 2"))),
            TableCreationParameters = TableCreationParameters(
                KeySchema = listOf(KeySchema(AttributeName.of("key"), KeyType.HASH)),
                TableName = TableName.of("test"),
                AttributeDefinitions = listOf(AttributeDefinition(AttributeName.of("key"), AttributeType = S)),
                BillingMode = PAY_PER_REQUEST
            )
        )

        println(awsClientRequest?.bodyString())
        println(http4kClientRequest?.bodyString())
        
        assertThat(http4kClientRequest?.header("X-Amz-Target"), equalTo(awsClientRequest?.header("X-Amz-Target")))
        assertThat(http4kClientRequest?.clientToken, equalTo(awsClientRequest?.clientToken))
        assertThat(http4kClientRequest?.inputCompressionType, equalTo(awsClientRequest?.inputCompressionType))
        assertThat(http4kClientRequest?.inputFormat, equalTo(awsClientRequest?.inputFormat))
        assertThat(http4kClientRequest?.inputFormatOptions, equalTo(awsClientRequest?.inputFormatOptions))
        assertThat(http4kClientRequest?.s3BucketSource, equalTo(awsClientRequest?.s3BucketSource))
        assertThat(http4kClientRequest?.tableCreationParameters, equalTo(awsClientRequest?.tableCreationParameters))

    }

    @Test
    fun `requests a table import from S3`() {
        val aws = configAwsEnvironment()
        val ddb = DynamoDb.Http(aws.region, { aws.credentials }, JavaHttpClient().debug(debugStream = true))

        val clientToken = ClientToken.random()
        val tableCreationParameters = TableCreationParameters(
            KeySchema = listOf(KeySchema(AttributeName.of("UPRN"), KeyType.HASH)),
            TableName = TableName.of("ac-test-dynamodb-import-1"),
            AttributeDefinitions = listOf(AttributeDefinition(AttributeName.of("UPRN"), AttributeType = S)),
            BillingMode = PAY_PER_REQUEST
        )
        val response = ddb.importTable(
            ClientToken = clientToken,
            InputCompressionType = InputCompressionType.NONE,
            S3BucketSource = S3BucketSource(S3Bucket = "i-do-not-exist"),
            InputFormat = InputFormat.CSV,
            InputFormatOptions = InputFormatOptions(CsvOptions(Delimiter = ',')),
            TableCreationParameters = tableCreationParameters
        ).successValue()

        assertThat(response.ImportTableDescription.ClientToken, equalTo(clientToken))
        assertThat(response.ImportTableDescription.CloudWatchLogGroupArn, absent())
        assertThat(response.ImportTableDescription.EndTime, absent())
        assertThat(response.ImportTableDescription.ErrorCount, equalTo(0))
        assertThat(response.ImportTableDescription.FailureCode, absent())
        assertThat(response.ImportTableDescription.FailureMessage, absent())
        assertThat(response.ImportTableDescription.ImportArn?.awsService, equalTo(AwsService.of("dynamodb")))
        assertThat(response.ImportTableDescription.ImportStatus, equalTo(ImportStatus.IN_PROGRESS))
        assertThat(response.ImportTableDescription.InputCompressionType, equalTo(InputCompressionType.NONE))
        assertThat(response.ImportTableDescription.InputFormat, equalTo(InputFormat.CSV))
        assertThat(
            response.ImportTableDescription.InputFormatOptions,
            equalTo(InputFormatOptions(CsvOptions(Delimiter = ',')))
        )
        assertThat(response.ImportTableDescription.ProcessedItemCount, present(greaterThanOrEqualTo(0)))
        assertThat(response.ImportTableDescription.ProcessedSizeBytes, absent())
        assertThat(response.ImportTableDescription.S3BucketSource?.S3Bucket, equalTo("i-do-not-exist"))
        assertThat(response.ImportTableDescription.StartTime, present())
        assertThat(response.ImportTableDescription.TableArn?.awsService, equalTo(AwsService.of("dynamodb")))
        assertThat(response.ImportTableDescription.TableCreationParameters, equalTo(tableCreationParameters))
        assertThat(response.ImportTableDescription.TableId, present())
    }

   
}

private fun Request.json() = adapter.fromJson(this.bodyString())

private val Request.clientToken: String? get() = json()?.get("ClientToken")?.let { it as String }
private val Request.s3BucketSource: Map<String, String>? get() = json()?.get("S3BucketSource")?.let { it as Map<String, String> }
private val Request.inputCompressionType: String? get() = json()?.get("InputCompressionType")?.let { it as String }
private val Request.inputFormat: String? get() = json()?.get("InputFormat")?.let { it as String }
private val Request.inputFormatOptions: Map<String, String>? get() = json()?.get("InputFormatOptions")?.let { it as Map<String, String> }
private val Request.tableCreationParameters: Map<String, String>? get() = json()?.get("TableCreationParameters")?.let { it as Map<String, String> }

private val moshi = Moshi.Builder().add(MapAdapter).build()
private val adapter: JsonAdapter<Map<String, Any>> = moshi.adapter(
    Types.newParameterizedType(
        MutableMap::class.java,
        String::class.java,
        Any::class.java
    )
)

private fun http4kClient(http: HttpHandler) = DynamoDb.Http(
    org.http4k.connect.amazon.core.model.Region.AF_SOUTH_1,
    { AwsCredentials("id", "secret") },
    http
)

private fun awsClient(http: HttpHandler) =
    DynamoDbClient.builder()
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("id", "secret")))
        .region(Region.AF_SOUTH_1)
        .httpClient(AwsSdkClient(http))
        .build()

private fun recordingRequest(recordRequest: (Request) -> Unit): HttpHandler =
    { r -> Response(Status.OK).body("{}").also { recordRequest(r) } }
