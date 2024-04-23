package org.http4k.connect.amazon.sqs

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import java.time.Duration

class RealSQSTest : SQSContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
    override val retryTimeout: Duration = Duration.ofMinutes(1)
    override fun waitABit() {
        Thread.sleep(10000)
    }
}
