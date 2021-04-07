package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.s3.action.S3BucketAction
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.time.Clock

fun S3Bucket.Companion.Http(
    bucketName: BucketName,
    bucketRegion: Region,
    credentialsProvider: () -> AwsCredentials,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = object : S3Bucket {
    private val signedHttp = signAwsRequests(bucketRegion, credentialsProvider, clock, payloadMode, "$bucketName.").then(http)

    override fun <R> invoke(action: S3BucketAction<R>) = action.toResult(signedHttp(action.toRequest()))
}

fun S3Bucket.Companion.Http(
    bucketName: BucketName,
    bucketRegion: Region,
    env: Map<String, String> = System.getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = Http(bucketName, bucketRegion, env.awsCredentials(), http, clock, payloadMode)
