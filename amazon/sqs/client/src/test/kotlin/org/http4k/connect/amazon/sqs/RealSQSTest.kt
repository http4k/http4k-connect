package org.http4k.connect.amazon.sqs

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealSQSTest : SQSContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
    override fun waitABit() {
        Thread.sleep(10000)
    }
}
