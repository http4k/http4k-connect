package org.http4k.connect.amazon.sts

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.filter.debug

class RealSTSTest : STSContract(JavaHttpClient().debug()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment(service)
}
