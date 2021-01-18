package org.http4k.connect.amazon.sqs

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealSNSTest : SNSContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
}
