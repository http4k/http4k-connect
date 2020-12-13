package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock

fun S3.Bucket.Companion.Http(bucketName: BucketName,
                             scope: AwsCredentialScope,
                             credentialsProvider: () -> AwsCredentials,
                             rawHttp: HttpHandler = JavaHttpClient(),
                             clock: Clock = Clock.systemDefaultZone(),
                             payloadMode: Payload.Mode = Payload.Mode.Signed) = object : S3.Bucket {
    private val http = SetBaseUriFrom(Uri.of("https://$bucketName.s3.${scope.region}.amazonaws.com"))
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override fun <R> invoke(request: S3BucketAction<R>) = request.toResult(http(request.toRequest(Region.of(scope.region))))
}
