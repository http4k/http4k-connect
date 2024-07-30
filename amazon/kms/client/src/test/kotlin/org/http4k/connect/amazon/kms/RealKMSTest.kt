package org.http4k.connect.amazon.kms

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealKMSTest : KMSContract(), RealAwsEnvironment {
    override val http = JavaHttpClient()

    override val aws get() = configAwsEnvironment()
}
