package org.http4k.connect.amazon.kms

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import org.junit.jupiter.api.Disabled

@Disabled
class RealKMSTest : KMSContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment(service)
}
