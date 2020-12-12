package org.http4k.connect.amazon.secretsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.filter.debug

class RealSecretsManagerTest : SecretsManagerContract(JavaHttpClient().debug()) {
    override val aws get() = configAwsEnvironment(service)
}


