package org.http4k.connect.amazon.systemsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.filter.debug

class RealSystemsManagerTest : SystemsManagerContract(JavaHttpClient().debug()) {
   override val aws get() = configAwsEnvironment(service)
}
