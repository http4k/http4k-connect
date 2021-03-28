package org.http4k.connect.amazon.cloudfront

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.AwsEnvironment
import org.http4k.connect.amazon.dynamodb.DynamoDbContract
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.assumeDockerDaemonRunning
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.testcontainers.dynamodb.DynaliteContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName.parse
import java.time.Duration

@Testcontainers
class LocalDynamoTest : DynamoDbContract(Duration.ofSeconds(1)) {

    init {
        assumeDockerDaemonRunning()
    }

    override val http by lazy {
        SetBaseUriFrom(Uri.of(dynamo.endpointConfiguration.serviceEndpoint))
            .then(JavaHttpClient())
    }

    @Container
    val dynamo =
        DynaliteContainer(parse("quay.io/testcontainers/dynalite:v1.2.1-1")).apply {
            start()
        }

    override val aws: AwsEnvironment by lazy {
        val credentials = dynamo.credentials.credentials
        AwsEnvironment(
            AwsCredentials(credentials.awsAccessKeyId, credentials.awsSecretKey),
            Region.of("us-east-1")
        )
    }


    override fun `transactional items`() {
        // not supported by dynalite
    }

    override fun `partiSQL operations`() {
        // not supported by dynalite
    }
}
