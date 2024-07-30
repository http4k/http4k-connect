package org.http4k.connect.amazon.evidently

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.filter.debug

class RealEvidentlyTest : EvidentlyContract(), RealAwsEnvironment {
    override val http = JavaHttpClient()

    override val aws get() = configAwsEnvironment()
}
