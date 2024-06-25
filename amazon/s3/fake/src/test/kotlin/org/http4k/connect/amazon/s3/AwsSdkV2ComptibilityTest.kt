package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsSdkClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.core.then
import org.http4k.filter.RequestFilters
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.StorageClass
import software.amazon.awssdk.services.s3.model.Tier

private val BUCKET_NAME = "aohara-test"

class AwsSdkV2ComptibilityTest {

    private val fake = FakeS3()

    private val key = "bar.txt"

    private val filter = RequestFilters.Tap {
        println(it)
    }

    private val client = S3Client.builder()
        .credentialsProvider {
            val creds = configAwsEnvironment().credentials
            AwsBasicCredentials.create(creds.accessKey, creds.secretKey)
        }
        .region(Region.US_EAST_1)

//        .httpClient(AwsSdkClient(filter.then(fake)))
//        .credentialsProvider { AwsBasicCredentials.create("id", "secret") }
//        .region(Region.CA_CENTRAL_1)
        .build()

    @Test
    fun restore() {
        client.putObject({
            it.bucket(BUCKET_NAME)
            it.key(key)
            it.storageClass(StorageClass.GLACIER)
        }, RequestBody.fromString("foo"))

        client.restoreObject {
            it.bucket(BUCKET_NAME)
            it.key(key)
            it.restoreRequest { req ->
                req.days(2)
                req.glacierJobParameters {  param ->
                    param.tier(Tier.EXPEDITED)
                }
//                req.tier(Tier.EXPEDITED)
            }
        }
    }
}
