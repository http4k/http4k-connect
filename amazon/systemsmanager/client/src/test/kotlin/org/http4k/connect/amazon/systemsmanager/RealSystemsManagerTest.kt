package org.http4k.connect.amazon.systemsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment

class RealSystemsManagerTest : SystemsManagerContract(JavaHttpClient()) {
   override val aws get() = configAwsEnvironment(service)
}
