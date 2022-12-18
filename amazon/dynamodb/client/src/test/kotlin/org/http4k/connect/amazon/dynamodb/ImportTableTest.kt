package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.aws.AwsCredentials
import org.http4k.aws.AwsSdkClient
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.ImportTableRequest

class ImportTableTest {
    @Test
    fun `request using AWS client`() {
        var awsClientRequest: Request? = null
        val awsDdb = awsClient(recordingRequest { awsClientRequest = it })
        var http4kClientRequest: Request? = null
        val http4kDdb = http4kClient(recordingRequest { http4kClientRequest = it })

        awsDdb.importTable(
            ImportTableRequest.builder()
                .clientToken("client token")
                .s3BucketSource {
                    it
                        .s3Bucket("bucketName")
                        .s3BucketOwner("owner")
                        .s3KeyPrefix("prefix")
                }
                .build()
        )
        http4kDdb.importTable(TableName.of("table"))

        println(awsClientRequest)
        assertThat(awsClientRequest?.header("X-Amz-Target"), equalTo("DynamoDB_20120810.ImportTable"))
        assertThat(http4kClientRequest?.header("X-Amz-Target"), equalTo("DynamoDB_20120810.ImportTable"))

    }

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
}
