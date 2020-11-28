package org.http4k.connect.amazon.secretsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsTest
import org.http4k.connect.amazon.configAwsEnvironment

class RealSystemsManagerTest : SystemsManagerContract(JavaHttpClient()), RealAwsTest {
    override val aws get() = configAwsEnvironment(service)
}
