package org.http4k.connect.amazon.kms

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment

class RealKMSTest : KMSContract(JavaHttpClient()) {
   override val aws get() = configAwsEnvironment(service)
}
