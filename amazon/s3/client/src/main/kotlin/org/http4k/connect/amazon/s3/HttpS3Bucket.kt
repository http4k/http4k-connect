package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.s3.action.S3BucketAction
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.time.Clock

fun S3Bucket.Companion.Http(
    bucketName: BucketName,
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = object : S3Bucket {
    private val http = signAwsRequests(region, credentialsProvider, clock, payloadMode, "$bucketName.").then(rawHttp)

    override fun <R> invoke(action: S3BucketAction<R>) = action.toResult(http(action.toRequest()))
}

fun S3Bucket.Companion.Http(
    bucketName: BucketName,
    region: Region,
    env: Map<String, String> = System.getenv(),
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = Http(bucketName, region, env.awsCredentials(), rawHttp, clock, payloadMode)
