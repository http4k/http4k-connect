package org.http4k.connect.amazon.evidently

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealEvidentlyContract : EvidentlyContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
}
