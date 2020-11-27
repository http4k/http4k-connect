package org.http4k.filters

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload
import java.time.Clock

fun ClientFilters.AmazonRegionalJsonStack(scope: AwsCredentialScope,
                                          rawHttp: HttpHandler,
                                          credentialsProvider: () -> AwsCredentials,
                                          awsService: AwsService,
                                          clock: Clock = Clock.systemDefaultZone(),
                                          payloadMode: Payload.Mode = Payload.Mode.Signed): HttpHandler =
    SetBaseUriFrom(Uri.of("https://$awsService.${scope.region}.amazonaws.com/"))
        .then(SetAmazonJsonContentType())
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

private fun SetAmazonJsonContentType() = Filter { next ->
    {
        next(it.replaceHeader("Content-Type", "application/x-amz-json-1.1"))
    }
}
