package org.http4k.connect.amazon

import org.http4k.client.JavaHttpClient

interface RealAwsEnvironment : AwsContract {
    override val aws get() = configAwsEnvironment()
    override val http get() = JavaHttpClient()
}
