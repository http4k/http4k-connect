package org.http4k.connect.amazon.kms

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsTest
import org.http4k.connect.amazon.configAwsEnvironment

class RealKMSTest : KMSContract(JavaHttpClient()), RealAwsTest {
    override val aws get() = configAwsEnvironment(service)
}
