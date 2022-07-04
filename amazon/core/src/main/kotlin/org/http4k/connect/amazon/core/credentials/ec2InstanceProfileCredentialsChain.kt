package org.http4k.connect.amazon.core.credentials

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.format.httpBodyLens
import java.io.IOException

class Ec2InstanceMetadataClient(
    http: HttpHandler = JavaHttpClient(),
    host: Uri = Uri.of("http://169.254.169.254"))
{
    private val backend = ClientFilters.SetHostFrom(host).then(http)
    private val accessKeyRegex = Regex(""""AccessKeyId"\s*:\s*"(.+)"""")
    private val secretKeyRegex = Regex(""""SecretAccessKey"\s*:\s*"(.+)"""")
    private val sessionTokenRegex = Regex(""""Token"\s*:\s*"(.+)"""")

    private val credentialsLens = httpBodyLens(contentType = ContentType.APPLICATION_JSON)
        .map(nextIn = { json ->
            AwsCredentials(
                accessKey = accessKeyRegex.find(json)?.groupValues?.get(1) ?: return@map null,
                secretKey = secretKeyRegex.find(json)?.groupValues?.get(1) ?: return@map null,
                sessionToken = sessionTokenRegex.find(json)?.groupValues?.get(1)
            )
        })
        .toLens()

    fun getInstanceProfiles(): List<String>? {
        val response = Request(Method.GET, "/latest/meta-data/iam/security-credentials")
            .let(backend)

        return when(response.status) {
            Status.OK -> response.bodyString().lines().filter { it.trim().isNotEmpty() }
            Status.NOT_FOUND -> null
            Status.CONNECTION_REFUSED -> null  // not in EC2 environment
            else -> throw IOException("Error retrieving ec2 instance metadata")
        }
    }

    fun getCredentials(instanceProfile: String): AwsCredentials? {
        val response = Request(Method.GET, "/latest/meta-data/iam/security-credentials/$instanceProfile")
            .let(backend)

        return when(response.status) {
            Status.OK -> credentialsLens(response)
            Status.NOT_FOUND -> null
            Status.CONNECTION_REFUSED -> null  // not in EC2 environment
            else -> throw IOException("Error retrieving ec2 instance metadata")
        }
    }
}

fun CredentialsChain.Companion.Ec2InstanceProfile(
    metadataClient: Ec2InstanceMetadataClient = Ec2InstanceMetadataClient()
) = CredentialsChain {
    metadataClient.getInstanceProfiles().orEmpty()
        .asSequence()
        .mapNotNull { profile -> metadataClient.getCredentials(profile) }
        .firstOrNull()
}
