package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsSdkClient
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.then
import org.http4k.filter.RequestFilters
import org.http4k.filter.ResponseFilters
import org.http4k.filter.debug
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.StorageClass
import software.amazon.awssdk.services.s3.model.Tier

class AwsSdkV2ComptibilityTest {

    private val fake = FakeS3()

    private val filter = RequestFilters.Tap {
        println(it)
    }

    private val client = S3Client.builder()
        .httpClient(AwsSdkClient(filter.then(fake)))
        .credentialsProvider { AwsBasicCredentials.create("id", "secret") }
        .region(Region.CA_CENTRAL_1)
        .build()

    @Test
    fun restore() {
        client.createBucket {
            it.bucket("foo")
        }

        client.putObject({
            it.bucket("foo")
            it.key("bar.txt")
            it.storageClass(StorageClass.GLACIER)
        }, RequestBody.fromString("foo"))

        client.restoreObject {
            it.bucket("foo")
            it.key("bar.txt")
            it.restoreRequest { req ->
                req.days(2)
                req.tier(Tier.EXPEDITED)
            }
        }
    }
}
