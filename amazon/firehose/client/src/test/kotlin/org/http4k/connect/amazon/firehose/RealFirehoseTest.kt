package org.http4k.connect.amazon.firehose

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealFirehoseTest : FirehoseContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
}
