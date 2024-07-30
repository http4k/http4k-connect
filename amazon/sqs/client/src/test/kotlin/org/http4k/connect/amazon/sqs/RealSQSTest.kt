package org.http4k.connect.amazon.sqs

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import java.time.Duration

class RealSQSTest : SQSContract(), RealAwsEnvironment {
    override val http = JavaHttpClient()


    override val retryTimeout: Duration = Duration.ofMinutes(1)
    override fun waitABit() {
        Thread.sleep(10000)
    }
}
