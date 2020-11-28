package org.http4k.connect.amazon.secretsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsTest
import org.http4k.connect.amazon.configAwsEnvironment

class RealSecretsManagerTest : SecretsManagerContract(JavaHttpClient()), RealAwsTest {
    override val aws get() = configAwsEnvironment(service)
}
