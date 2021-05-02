package org.http4k.connect.amazon.dynamodb

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.assumeDockerDaemonRunning
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.debug
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.DockerImageName.parse
import java.time.Duration

@Testcontainers
class LocalDynamoTest : DynamoDbContract(Duration.ofSeconds(1)) {
    init {
        assumeDockerDaemonRunning()
    }

    override val http by lazy {
        SetBaseUriFrom(Uri.of("http://localhost:${dynamo.getMappedPort(8000)}"))
            .then(JavaHttpClient().debug())
    }

    val parse: DockerImageName = parse("amazon/dynamodb-local:1.15.0")

    @Container
    val dynamo: GenericContainer<*> = TODO()//GenericContainer<GenericContainer<*>>(parse).withExposedPorts(8000)

    override val aws = fakeAwsEnvironment
}
