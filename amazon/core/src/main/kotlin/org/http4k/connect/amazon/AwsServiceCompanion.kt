package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.filter.Payload
import java.time.Clock

/**
 * Shared infra for all AWS services.
 */
open class AwsServiceCompanion(private val awsServiceName: String) {
    fun signAwsRequests(
        region: Region,
        credentialsProvider: () -> AwsCredentials,
        clock: Clock,
        payloadMode: Payload.Mode,
        servicePrefix: String = "") =
        SetHostFrom(Uri.of("https://$servicePrefix$awsServiceName.$region.amazonaws.com"))
            .then(ClientFilters.AwsAuth(
                AwsCredentialScope(region.value, awsServiceName),
                credentialsProvider, clock, payloadMode))
}
