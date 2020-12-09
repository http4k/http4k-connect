package org.http4k.connect.amazon.kms

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealKMSTest : KMSContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws = configAwsEnvironment(service)
}
