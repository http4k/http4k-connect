package org.http4k.connect.amazon.sts

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealSTSTest : STSContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment(service)
}
