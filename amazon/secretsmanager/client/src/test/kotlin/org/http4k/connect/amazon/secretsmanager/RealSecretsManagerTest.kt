package org.http4k.connect.amazon.secretsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import java.util.UUID

class RealSecretsManagerTest : SecretsManagerContract(), RealAwsEnvironment {
    override val http = JavaHttpClient()

    override val aws get() = configAwsEnvironment()

    override val propogateTime: Long = 5000

    override val nameOrArn = UUID.randomUUID().toString()
}


